package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy

import android.content.Context
import android.util.Log
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception.PharmacyNameAlreadyInUse
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PharmacyServiceImpl(private val context : Context) : ParmacyService{

    override fun addPharmacy(pharmacy: AddPharmacyDto, callback: (Boolean, Exception?) -> Unit) {
        // Send the pharmacy to the server
        // If the server responds with a success, call callback(true, null)
        // If the server responds with an error, call callback(false, "Error message")
        thread{
            val apiUrl: String = ConfigClass.getUrl(context)
            val addPharmacyUrl = "$apiUrl/pharmacy/add"

            try {
                val url = URL(addPharmacyUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                // Set request headers (optional)login
                connection.setRequestProperty("Content-Type", "application/json")

                // Construct the request body as JSON
                val requestBody = "{\"name\": \"${pharmacy.getName()}\", \"latitude\": ${pharmacy.getLatitude()}, " +
                        "\"longitude\": ${pharmacy.getLongitude()}, \"picturePath\": \"${pharmacy.getPicturePath()}\", " +
                        "\"pictureExtension\": \"${pharmacy.getPictureExtension()}\"}"

                val outputStream = connection.outputStream
                outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    // Read and handle successful response
                    val inputStream = connection.inputStream
                    val responseBody = inputStream.bufferedReader().use { it.readText() }
                    Log.d("Pharmacy", "Pharmacy added successfully: $responseBody")
                    inputStream.close()
                } else {
                    // Handle error response
                    val errorStream = connection.errorStream
                    val errorBody = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    Log.e("Pharmacy", "Error adding pharmacy with HTTP status code: $responseCode. Error body: $errorBody")
                    errorStream?.close()

                    if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                        throw PharmacyNameAlreadyInUse(pharmacy.getName())
                    }else{
                        throw RuntimeException("Error adding pharmacy: $errorBody")
                    }
                }


                callback(true, null)
            }catch (e: Exception){
                callback(false, e)
                Log.e("Pharmacy", "Error adding pharmacy: ${e.message}")
            }
        }
    }

}