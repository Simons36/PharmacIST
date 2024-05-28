package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.Logging
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception.PharmacyNameAlreadyInUse
import java.io.File
import java.net.HttpURLConnection

class PharmacyServiceImpl(private val context : Context) : ParmacyService {

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

    override suspend fun addPharmacy(pharmacy: AddPharmacyDto){
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

}