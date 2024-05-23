package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.OnMapReadyCallback
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception.PharmacyNameAlreadyInUse
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.net.HttpURLConnection

class PharmacyServiceImpl(private val context : Context) : ParmacyService{

    override fun addPharmacy(pharmacy: AddPharmacyDto, callback : (Boolean, Exception?) -> Unit)  {
        // Send the pharmacy to the server
        // If the server responds with a success, call callback(true, null)
        // If the server responds with an error, call callback(false, "Error message")
        val apiUrl: String = ConfigClass.getUrl(context)
        val addPharmacyUrl = "$apiUrl/pharmacy/add"

        val argumentsMap = mutableMapOf<String, String>(
            "name" to pharmacy.getName(),
            "latitude" to pharmacy.getLatitude().toString(),
            "longitude" to pharmacy.getLongitude().toString()
        )

        if (pharmacy.getPicturePath() != null) {
            argumentsMap["picturePath"] = pharmacy.getPicturePath()!!
        }
        if (pharmacy.getPictureExtension() != null) {
            argumentsMap["pictureExtension"] = pharmacy.getPictureExtension()!!
        }


        UtilFunctions.sendHttpRequest(addPharmacyUrl, "POST", argumentsMap, HttpURLConnection.HTTP_CREATED) { success, responseCode, responseBodyOrExceptionMessage ->
            if(success){
                Log.d("Pharmacy", "Pharmacy added successfully: $responseBodyOrExceptionMessage")
                callback(true, null)
            }else{
                Log.e("Pharmacy", "Error adding pharmacy with HTTP status code: $responseCode. Error body: $responseBodyOrExceptionMessage")
                if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                    callback(false, PharmacyNameAlreadyInUse(pharmacy.getName()))
                }else{
                    callback(false, RuntimeException("Error adding pharmacy: $responseBodyOrExceptionMessage"))
                }
            }
        }


    }

}