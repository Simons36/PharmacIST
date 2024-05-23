package pt.ulisboa.tecnico.cmov.pharmacist.auth;

import android.content.Context
import android.util.Log
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.AccountNotFoundException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.WrongPasswordException
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class AuthenticationServiceImpl(private  val context : Context) : AuthenticationService{


    override fun login(email: String, password: String, callback : (Boolean, String?) -> Unit) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val loginUrl = "$apiUrl/auth/login"

        val argumentsMap = mutableMapOf<String, String>(
            "email" to email,
            "password" to password
        )

        UtilFunctions.sendHttpRequest(loginUrl, "POST", argumentsMap, HttpURLConnection.HTTP_OK) { success, responseCode, responseBodyOrExceptionMessage ->
            if(success){
                Log.d("Auth", "Login successful: $responseBodyOrExceptionMessage")
                callback(true, null)
            }else{
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

    override fun register(username : String, email : String, password: String){
        throw Exception("Not implemented yet.");
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
    
        // thread{

        //     val apiUrl: String = ConfigClass.getUrl(context)
        //     val loginUrl = "$apiUrl/auth/login"

        //     try {
        //         val url = URL(loginUrl)
        //         val connection = url.openConnection() as HttpURLConnection
        //         connection.requestMethod = "POST"
        //         connection.doOutput = true

        //         // Set request headers (optional)
        //         connection.setRequestProperty("Content-Type", "application/json")

        //         // Construct the request body as JSON
        //         val requestBody = "{\"email\": \"$email\", \"password\": \"$password\"}"

        //         // Write request body to the connection's output stream
        //         val outputStream: OutputStream = connection.outputStream
        //         outputStream.write(requestBody.toByteArray(StandardCharsets.UTF_8))
        //         outputStream.close()

        //         // Get response from the server
        //         val responseCode = connection.responseCode
        //         if (responseCode == HttpURLConnection.HTTP_OK) {
        //             // Read and handle successful response
        //             val inputStream: InputStream = connection.inputStream
        //             val responseBody = inputStream.bufferedReader().use(BufferedReader::readText)
        //             Log.d("Auth", "Login successful: $responseBody")
        //             inputStream.close()
        //         } else {
        //             // Handle error response
        //             val errorStream: InputStream = connection.errorStream
        //             val errorBody = errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
        //             Log.e("Auth", "Login failed with HTTP status code: $responseCode. Error body: $errorBody")
        //             errorStream?.close()

        //             // Now throw exceptions, according to error

        //             if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){ //this means wrong password
        //                 throw WrongPasswordException(email);
        //             } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
        //                 throw AccountNotFoundException(email);
        //             }
        //         }

        //         connection.disconnect()

        //         callback(true, null)
        //     } catch (e: Exception) {
        //         callback(false, e.message)
        //         Log.e("Auth", "Error during login: ${e.message}", e)
        //     }

        // }
}
