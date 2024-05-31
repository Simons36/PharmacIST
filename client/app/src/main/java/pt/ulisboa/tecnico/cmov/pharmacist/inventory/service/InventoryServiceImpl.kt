package pt.ulisboa.tecnico.cmov.pharmacist.inventory.service

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.DisplayMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass

object InventoryServiceImpl {

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

    suspend fun getPharmacyInventory(pharmacyName: String, context: Context): List<DisplayMedicineDto> {
        val apiUrl: String = ConfigClass.getUrl(context)
        val getPharmacyInventoryURL = "$apiUrl/medicine/inventory/${pharmacyName}"

        val response = httpClient.get(getPharmacyInventoryURL){

        }


        val jsonResponse = response.bodyAsText()

        // Parse the JSON response using kotlinx.serialization
        val medicines: List<DisplayMedicineDto> = Json.decodeFromString(jsonResponse)

        // Return the parsed list
        return medicines

    }

}