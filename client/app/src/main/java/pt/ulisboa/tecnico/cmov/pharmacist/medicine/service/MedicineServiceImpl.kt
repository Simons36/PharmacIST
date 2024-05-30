package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass


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


}