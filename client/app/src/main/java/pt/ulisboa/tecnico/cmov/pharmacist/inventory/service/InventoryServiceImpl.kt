package pt.ulisboa.tecnico.cmov.pharmacist.inventory.service

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.DisplayMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.OtherMedicineDto
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

    suspend fun addStock(pharmacyName: String, medicineName: String, quantity: Int, context: Context){
        val apiUrl: String = ConfigClass.getUrl(context)
        val addStockURL = "$apiUrl/medicine/stock/add"


        // Create temp dto
        val dto = TempDto(pharmacyName, medicineName, quantity)

        val response = httpClient.post(addStockURL){
            contentType(ContentType.Application.Json)
            setBody(dto)
        }

        val jsonResponse = response.bodyAsText()

        Log.d("AddStock", jsonResponse)
    }

    suspend fun removeStock(pharmacyName: String, medicineName: String, quantity: Int, context: Context){
        val apiUrl: String = ConfigClass.getUrl(context)
        val removeStockURL = "$apiUrl/medicine/stock/remove"
        Log.i("DEBUG", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa")

        val dto = TempDto(pharmacyName, medicineName, quantity)

        val response = httpClient.post(removeStockURL){
            contentType(ContentType.Application.Json)
            setBody(dto)

        }

        val jsonResponse = response.bodyAsText()

        Log.d("RemoveStock", jsonResponse)
    }

}