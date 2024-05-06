package pt.ulisboa.tecnico.cmov.pharmacist.auth;

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class Auth {

    fun login(context: Context, email: String, password: String) {
        val apiUrl: String = ConfigClass.getUrl(context)
        val loginUrl = "$apiUrl/auth/login"

        // Use Kotlin Coroutine to perform network operation asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(loginUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                // Set request headers (optional)
                connection.setRequestProperty("Content-Type", "application/json")

                // Construct the request body as JSON
                val requestBody = "{\"email\": \"$email\", \"password\": \"$password\"}"

                // Write request body to the connection's output stream
                val outputStream: OutputStream = connection.outputStream
                outputStream.write(requestBody.toByteArray(StandardCharsets.UTF_8))
                outputStream.close()

                // Get response from the server
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read and handle successful response
                    val inputStream: InputStream = connection.inputStream
                    val responseBody = inputStream.bufferedReader().use(BufferedReader::readText)
                    Log.d("Auth", "Login successful: $responseBody")
                    inputStream.close()
                } else {
                    // Handle error response
                    val errorStream: InputStream = connection.errorStream
                    val errorBody = errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                    Log.e("Auth", "Login failed with HTTP status code: $responseCode. Error body: $errorBody")
                    errorStream?.close()
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("Auth", "Error during login: ${e.message}", e)
            }
        }
    }
}
