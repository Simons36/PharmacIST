package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception.PharmacyNameAlreadyInUse
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response.UpdatePharmaciesStatusResponse
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection

object PharmacyServiceImpl : ParmacyService {

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

    override suspend fun addPharmacy(pharmacy: AddPharmacyDto, context: Context){
        // Send the pharmacy to the server
        // If the server responds with a success, call callback(true, null)
        // If the server responds with an error, call callback(false, "Error message")

        val apiUrl: String = ConfigClass.getUrl(context)
        val addPharmacyUrl = "$apiUrl/pharmacy/add"

        val response : HttpResponse = this.httpClient.post(addPharmacyUrl){
            contentType(ContentType.Application.Json)
            if(pharmacy.getPicturePath() != null){

                setBody(MultiPartFormDataContent(
                    formData {
                        append("photo", File(pharmacy.getPicturePath()!!).readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
                            append(HttpHeaders.ContentDisposition, "filename=\"${pharmacy.getName()}.png\"")
                        })
                        append("name", pharmacy.getName())

                        if(pharmacy.getAddress() != null){
                            append("address", pharmacy.getAddress()!!)
                        }else{
                            append("latitude", pharmacy.getLatitude()!!)
                            append("longitude", pharmacy.getLongitude()!!)
                        }

                    },

                ))
            }else{
                setBody(pharmacy)
            }
        }

        // 201 Created -> means success
        // 409 Conflict -> means the name is already in use
        if(response.status.value == HttpURLConnection.HTTP_CONFLICT){
            throw PharmacyNameAlreadyInUse(pharmacy.getName())
        }else if(response.status.value != HttpURLConnection.HTTP_CREATED){
            throw RuntimeException("Error adding pharmacy: ${response.bodyAsText()}")
        }

    }

    override suspend fun getPharmacyPhoto(pharmacyName : String, context: Context) : String{
        val apiUrl: String = ConfigClass.getUrl(context)
        val getPharmacyPhotoUrl = "$apiUrl/pharmacy/photo/$pharmacyName"

        val response : HttpResponse = this.httpClient.get(getPharmacyPhotoUrl)

        if(response.status.value != HttpURLConnection.HTTP_OK){
            throw RuntimeException("Error getting pharmacy photo: ${response.bodyAsText()}")
        }

        // Parse the JSON response
        val jsonResponse = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonResponse)
        val dataArray = jsonElement.jsonObject["data"]?.jsonArray ?: throw RuntimeException("Invalid response format")

        // Convert the JSON array to a ByteArray
        val byteArray = ByteArray(dataArray.size) { index ->
            dataArray[index].jsonPrimitive.int.toByte()
        }

        // Define the file name and path
        val fileName = "$pharmacyName.png"
        val file = File(context.filesDir, fileName)

        // Write the byte array to the file
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
        }

        return file.absolutePath

    }

    override suspend fun syncPharmacyInfo(knownVersion : Int, context: Context) : UpdatePharmaciesStatusResponse{
        val apiUrl: String = ConfigClass.getUrl(context)
        val updatePharmaciesStatusUrl = "$apiUrl/pharmacy/sync/version/${knownVersion}"

        val response : HttpResponse = this.httpClient.get(updatePharmaciesStatusUrl){
            headers {
                append("Authorization", "Bearer ${UtilFunctions.getJwtTokenFromSharedPreferences(context)}")
            }
        }
        // server will response with two lists: one list for remove (pharmacies to remove)
        // and another list for add (pharmacies to add)

        if(response.status.value != HttpURLConnection.HTTP_OK){
            throw RuntimeException("Error updating pharmacies status: ${response.bodyAsText()}")
        }

        val jsonResponse = response.bodyAsText()
        Log.i("DEBUG", jsonResponse)
        val deserialized =  Json.decodeFromString(UpdatePharmaciesStatusResponse.serializer(), jsonResponse)
        Log.i("DEBUG", deserialized.toString())
        return deserialized

    }

    override suspend fun addFavoritePharmacy(pharmacyName: String, context: Context) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val addFavoritePharmacyUrl = "$apiUrl/user/favorite/add/$pharmacyName"

        val response: HttpResponse = this.httpClient.post(addFavoritePharmacyUrl) {
            headers {
                append(
                    "Authorization",
                    "Bearer ${UtilFunctions.getJwtTokenFromSharedPreferences(context)}"
                )
            }

        }

        if (response.status.value != HttpURLConnection.HTTP_OK) {
            throw RuntimeException("Error adding pharmacy to favorite: ${response.bodyAsText()}")
        }
    }

    override suspend fun removeFavoritePharmacy(pharmacyName: String, context: Context) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val removeFavoritePharmacyUrl = "$apiUrl/user/favorite/remove/$pharmacyName"

        val response: HttpResponse = this.httpClient.post(removeFavoritePharmacyUrl) {
            headers {
                append(
                    "Authorization",
                    "Bearer ${UtilFunctions.getJwtTokenFromSharedPreferences(context)}"
                )
            }

        }

        if (response.status.value != HttpURLConnection.HTTP_OK) {
            throw RuntimeException("Error removing pharmacy from favorite: ${response.bodyAsText()}")
        }

    }

}