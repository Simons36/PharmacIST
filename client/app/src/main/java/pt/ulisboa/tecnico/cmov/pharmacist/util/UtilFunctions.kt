package pt.ulisboa.tecnico.cmov.pharmacist.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class UtilFunctions {
    companion object{
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap =
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun Int.dpToPx(resources : Resources): Int = (this * resources.displayMetrics.density).toInt()

        fun saveToInternalStorage(bitmapImage: Bitmap, imageFilename : String, activity: Activity): String {
            val cw = ContextWrapper(activity.applicationContext)
            // path to /data/data/yourapp/app_data/imageDir
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            // Create imageDir
            val myPath = File(directory, imageFilename)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(myPath)
                // save image to the directory
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos!!.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return directory.absolutePath + "/" + imageFilename
        }

        fun saveJwtTokenToSharedPreferences(jwtToken: String, context: Context) {
            val sharedPref = context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString("jwtToken", jwtToken)
                commit()
            }
        }

        fun getJwtTokenFromSharedPreferences(context: Context): String? {
            val sharedPref = context.getSharedPreferences("jwtToken", Context.MODE_PRIVATE)
            return sharedPref.getString("jwtToken", null)
        }

        fun showDialog(title : String, message : String, context: Context, onYesClicked : () -> Unit, onNoClicked : () -> Unit){
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)

            builder.setPositiveButton("Yes") { dialog, _ ->
                // Set the address to the current location
                onYesClicked()



                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                onNoClicked()
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun sendHttpRequest(
            providedUrl: String,
            requestMethod: String,
            requestBodyArgs: Map<String, String>,
            httpSuccessCode: Int,
            callback: (Boolean, Int?, String) -> Unit
        ) {
            thread {
                try {
                    val url = URL(providedUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = requestMethod
                    connection.doOutput = true

                    // Set request headers (optional)
                    connection.setRequestProperty("Content-Type", "application/json")

                    // Construct the request body as JSON
                    var requestBody = "{"
                    for ((key, value) in requestBodyArgs) {
                        if(key == "picturePath"){
                            val file = File(value)
                            val fileInputStream = file.inputStream()
                            val fileData = fileInputStream.readBytes()
                            connection.setRequestProperty("Content-Type", "application/octet-stream")
                            connection.setRequestProperty("Content-Length", fileData.size.toString())
                            connection.outputStream.write(fileData)
                            fileInputStream.close()
                            continue

                        }
                        requestBody += "\"$key\": \"$value\", "
                    }
                    requestBody = requestBody.dropLast(2) + "}"

                    val outputStream = connection.outputStream
                    outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                    outputStream.close()

                    val responseCode = connection.responseCode
                    if (responseCode == httpSuccessCode) {
                        // Read and handle successful response
                        val inputStream = connection.inputStream
                        val responseBody = inputStream.bufferedReader().use { it.readText() }
                        callback(true, responseCode, responseBody)
                        inputStream.close()
                    } else {
                        // Handle error response
                        val errorStream = connection.errorStream
                        val errorBody = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                        callback(false, responseCode, errorBody)
                        errorStream?.close()
                    }

                    connection.disconnect()
                } catch (e: Exception) {
                    callback(false, null, e.message!!)
                }
            }
        }

    }

}