package pt.ulisboa.tecnico.cmov.pharmacist.user.service

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions

object UserServiceImpl {

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

    suspend fun getFavorites(context: Context) : List<String> {
        val apiUrl: String = ConfigClass.getUrl(context)
        val getFavoritesURL = "$apiUrl/user/favorites"

        return try {
            val response: List<String> = MedicineServiceImpl.httpClient.get(getFavoritesURL) {
                headers {
                    append("Authorization", "Bearer ${UtilFunctions.getJwtTokenFromSharedPreferences(context)}")
                }
            }.body()
            response
        } catch (e: Exception) {
            // Handle exception (e.g., log the error, return an empty list, etc.)
            emptyList()
        }

    }


}