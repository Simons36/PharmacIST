package pt.ulisboa.tecnico.cmov.pharmacist

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.helper.PharmacyInfoDbHelper
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service.PharmacyServiceImpl
import java.io.File

class PharmacyInfoPanelActivity : AppCompatActivity(){

    private lateinit var pharmacyInfoDbHelper : PharmacyInfoDbHelper

    private lateinit var pharmacyNameTextView: TextView
    private lateinit var pharmacyAddressTextView: TextView
    private lateinit var pharmacyLatitudeTextView: TextView
    private lateinit var pharmacyLongitudeTextView: TextView
    private lateinit var imageViewPhoto : ImageView
    private lateinit var goToLocationButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy_information_panel)

        lifecycleScope.launch {

            pharmacyInfoDbHelper = PharmacyInfoDbHelper(applicationContext)


            // Get the pharmacy name from the intent
            val extras = intent.extras
            val pharmacyName = extras?.getString("pharmacyName")

            // make async call to get photo
            imageViewPhoto = findViewById<ImageView>(R.id.imageViewPhoto)
            var photoJob: Deferred<String>? = null
            var photoPath = pharmacyInfoDbHelper.getPharmacyPhotoPath(pharmacyName!!)

            if(photoPath == null){ // if photo is not in cache
                photoJob = async{
                    PharmacyServiceImpl.getPharmacyPhoto(pharmacyName, applicationContext)
                }
            }else{
                // load photo
                val photo = File(photoPath).readBytes()
                imageViewPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.size))
            }


            // Get address and latitude and longitude from the cache
            val pharmacy = pharmacyInfoDbHelper.getPharmacyInfo(pharmacyName)

            if(pharmacy != null){
                initNameLabel(pharmacy)
                initAddressLabel(pharmacy)
                initLatitudeLabel(pharmacy)
                initLongitudeLabel(pharmacy)
            }else{
                Toast.makeText(applicationContext, "Pharmacy information not available", Toast.LENGTH_SHORT).show()
                finish()
            }

            // Init back button
            val backButton = findViewById<FloatingActionButton>(R.id.backButton)
            backButton.setOnClickListener {
                finish()
            }

            // Init go to location button
            goToLocationButton = findViewById<FloatingActionButton>(R.id.btnGoToMapLocation)
            goToLocationButton.setOnClickListener {
                val data = Intent()
                data.putExtra("latitude", pharmacy!!.latitude)
                data.putExtra("longitude", pharmacy!!.longitude)
                setResult(Activity.RESULT_OK, data)
                finish()
            }

            if(photoJob != null){
                photoPath = photoJob.await()
                val photo = File(photoPath).readBytes()
                imageViewPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.size))

                // Lastly, save the photo path to the cache
                pharmacyInfoDbHelper.addPharmacyPhoto(pharmacyName, photoPath)
            }
        }



    }



    private fun initNameLabel(pharmacy: PharmacyDto){
        // Set the pharmacy name in the text view
        pharmacyNameTextView = findViewById<TextView>(R.id.labelName)
        val spannableStringName = SpannableString(pharmacy.name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableStringName.setSpan(boldSpan, 0, pharmacy.name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        pharmacyNameTextView.text = spannableStringName
    }

    private fun initAddressLabel(pharmacy: PharmacyDto){
        pharmacyAddressTextView = findViewById<TextView>(R.id.labelAddress)

        val addressText = StringBuilder()
        addressText.append("Address: ")

        if(pharmacy.address != null){
            addressText.append(pharmacy.address)
        }else{
            addressText.append("(not available)")
        }

        val spannableStringAddress = SpannableString(addressText.toString())

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableStringAddress.setSpan(boldSpan, 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        pharmacyAddressTextView.text = spannableStringAddress
    }

    private fun initLatitudeLabel(pharmacy: PharmacyDto){
        this.pharmacyLatitudeTextView = findViewById<TextView>(R.id.labelLatitude)

        val latLngText = StringBuilder()
        latLngText.append("Latitude: ")

        latLngText.append("${pharmacy.latitude}")

        val spannableStringLatLng = SpannableString(latLngText.toString())

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableStringLatLng.setSpan(boldSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        pharmacyLatitudeTextView.text = spannableStringLatLng
    }

    private fun initLongitudeLabel(pharmacy: PharmacyDto){
        this.pharmacyLongitudeTextView = findViewById<TextView>(R.id.labelLongitude)

        val latLngText = StringBuilder()
        latLngText.append("Longitude: ")

        latLngText.append("${pharmacy.longitude}")

        val spannableStringLatLng = SpannableString(latLngText.toString())

        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableStringLatLng.setSpan(boldSpan, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        pharmacyLongitudeTextView.text = spannableStringLatLng
    }

}