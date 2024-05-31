package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.AddMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception.MedicineAlreadyExistsException
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.exception.NoSuchPharmacyException
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import java.io.File
import java.net.HttpURLConnection


class MedicineServiceImpl : MedicineService {

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