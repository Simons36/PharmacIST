package pt.ulisboa.tecnico.cmov.pharmacist.auth

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import pt.ulisboa.tecnico.cmov.pharmacist.auth.dto.RegisterDto
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.AccountNotFoundException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.EmailAlreadyExistsException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.UsernameAlreadyExistsException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.WrongPasswordException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.util.ErrorResponse
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object AuthenticationServiceImpl : AuthenticationService {

    private val httpClient = HttpClient(Android) {
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    override fun login(email: String, password: String, context : Context, callback: (Boolean, String?) -> Unit) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val loginUrl = "$apiUrl/auth/login"

        val argumentsMap = mutableMapOf(
            "email" to email,
            "password" to password
        )

        UtilFunctions.sendHttpRequest(loginUrl, "POST", argumentsMap, HttpURLConnection.HTTP_OK) { success, responseCode, responseBodyOrExceptionMessage ->
            if (success) {
                Log.d("Auth", "Login successful: $responseBodyOrExceptionMessage")
                // Parse JSON string
                val jsonObject = JSONObject(responseBodyOrExceptionMessage)
                UtilFunctions.saveJwtTokenToSharedPreferences(jsonObject.getString("access_token"), context)
                callback(true, null)
            } else {
                Log.e("Auth", "Error during login with HTTP status code: $responseCode. Error body: $responseBodyOrExceptionMessage")
                when (responseCode) {
                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        callback(false, "Wrong password")
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        callback(false, "Account not found")
                    }
                    else -> {
                        callback(false, "Error during login: $responseBodyOrExceptionMessage")
                    }
                }
            }
        }
    }

    override suspend fun register(registerDto: RegisterDto, context: Context) {
        val apiUrl = ConfigClass.getUrl(context)
        val registerUrl = "$apiUrl/auth/register"

        val response = httpClient.post(registerUrl) {
            contentType(ContentType.Application.Json)
            setBody(registerDto)
        }

        val responseCode = response.status.value

        if(responseCode == HttpURLConnection.HTTP_CREATED){
            return;
        }else{
            if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                val responseBody = response.bodyAsText() // Get the response body as text
                Log.d("DEBUG", responseBody)
                val errorResponse = Json.decodeFromString<ErrorResponse>(responseBody) // Deserialize the response body



                if(errorResponse.field == "email"){
                    throw EmailAlreadyExistsException(registerDto.email);
                }else if(errorResponse.field == "username") {
                    throw UsernameAlreadyExistsException(registerDto.username);
                }
            }

            throw RuntimeException("Error during registration: ${response.bodyAsText()}")

        }

    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}
