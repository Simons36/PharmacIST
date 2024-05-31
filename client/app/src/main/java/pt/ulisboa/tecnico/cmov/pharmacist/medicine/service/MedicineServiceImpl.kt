package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.AddMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.DisplayMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception.MedicineAlreadyExistsException
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception.NoSuchPharmacyException
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.io.FileOutputStream
import java.io.File
import java.net.HttpURLConnection


object MedicineServiceImpl : MedicineService {

    val httpClient = HttpClient(Android) {
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun searchMedicines(query: String, context: Context): List<MedicineDTO> {
        val apiUrl: String = ConfigClass.getUrl(context)
        val searchMedicineURL = "$apiUrl/medicine/search"

        return try {
            val response: List<MedicineDTO> = httpClient.get(searchMedicineURL) {
                parameter("query", query)
            }.body()
            response
        } catch (e: Exception) {
            // Handle exception (e.g., log the error, return an empty list, etc.)
            emptyList()
        }
    }

    override suspend fun getAllMedicines(context: Context): List<MedicineDTO> {
        val apiUrl: String = ConfigClass.getUrl(context)
        val getAllMedicinesURL = "$apiUrl/medicine"

        return try {
            httpClient.get(getAllMedicinesURL).body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMedicinePhoto(medicineName: String, context: Context): ByteArray {
        val apiUrl: String = ConfigClass.getUrl(context)
        val getMedicinePhotoUrl = "$apiUrl/medicine/photo/$medicineName"

        val response: HttpResponse = this.httpClient.get(getMedicinePhotoUrl)

        if (response.status.value != 200) {
            throw RuntimeException("Error getting medicine photo: ${response.bodyAsText()}")
        }

        // Parse the JSON response
        val jsonResponse = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonResponse)
        val dataArray = jsonElement.jsonObject["data"]?.jsonArray
            ?: throw RuntimeException("Invalid response format")

        // Convert the JSON array to a ByteArray
        return ByteArray(dataArray.size) { index ->
            dataArray[index].jsonPrimitive.int.toByte()
        }
    }
    override suspend fun addMedicine(medicine: AddMedicineDto, context: Context) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val addMedicineURL = "$apiUrl/medicine/add"

        val response = httpClient.post(addMedicineURL) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("photo", File(medicine.photoPath).readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
                        })
                        append("name", medicine.name)
                        append("pharmacyName", medicine.pharmacyName)
                        append("quantity", medicine.quantity)
                        append("purpose", medicine.purpose)
                    }
                )
            )
        }

        when(response.status.value){
            HttpURLConnection.HTTP_CREATED -> {
                return;
            }
            HttpURLConnection.HTTP_CONFLICT -> {
                throw MedicineAlreadyExistsException(medicine.name)
            }
            HttpURLConnection.HTTP_NOT_FOUND -> {
                throw NoSuchPharmacyException(medicine.pharmacyName)
            }
            else -> {
                throw RuntimeException("Error adding medicine: ${response.bodyAsText()}")
            }
        }
    }





}