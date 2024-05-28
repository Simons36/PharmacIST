package pt.ulisboa.tecnico.cmov.pharmacist

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service.ParmacyService
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service.PharmacyServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDtoBuilder
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions.Companion.dpToPx
import java.io.IOException
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.exception.PharmacyNameAlreadyInUse
import pt.ulisboa.tecnico.cmov.pharmacist.util.ConfigClass
import pt.ulisboa.tecnico.cmov.pharmacist.util.placesautocomplete.PlaceAPI
import pt.ulisboa.tecnico.cmov.pharmacist.util.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import pt.ulisboa.tecnico.cmov.pharmacist.util.placesautocomplete.model.Place
import java.io.File


class AddPharmacyActivity() : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 1;

    private val TIME_SHOW_ADD_PHARMACY_SUCCESS: Long = 2000

    private lateinit var lastKnownLocation: Location;

    // Variables to check if the fields are populated
    // This is used to enable the confirm button, however one can
    // create a pharmacy with no picture (if picture field is not populated,
    // a warning message should be shown)
    private var nameEditTextPopulated : Boolean = false
    private var locationPopulated : Boolean = false
    private var picturePopulated : Boolean = false

    // Builder for the AddPharmacyDto
    private var addPharmacyDtoBuilder = AddPharmacyDtoBuilder()

    // Widgets
    private lateinit var confirmButton : Button
    private lateinit var cancelButton : Button
    private lateinit var pharmacyNameEditText : EditText
    private lateinit var addressEditText : AutoCompleteTextView
    private lateinit var setAddressToCurrentLocationButton : FloatingActionButton
    private lateinit var pickAddressFromMapButton : FloatingActionButton
    private lateinit var takePictureButton: Button
    private lateinit var openGalleryButton: Button
    private lateinit var cancelPhotoButton: FloatingActionButton

    // To launch the map activity to pick location of new pharmacy
    private lateinit var pickLocationFromMapLauncher: ActivityResultLauncher<Intent>

    // To launch camera and retrieve image
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    // To pick image from gallery
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    //Service to communicate with the server
    private lateinit var pharmacyService : ParmacyService



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pharmacy)

        // Init other activity launchers
        initPickLocationFromMapLauncher();
        initCameraLauncher();
        initGalleryLauncher();

        Log.i("DEBUG", "$nameEditTextPopulated")

        val extras = intent.extras
        if(extras != null){
            lastKnownLocation = extras.getParcelableCompat("lastKnownLocation", Location::class.java)!!

        }else{
            Toast.makeText(this, "Location services not available right now", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Init buttons, edit texts, etc.
        initWidgets()




        //Init the service
        pharmacyService = PharmacyServiceImpl(this)
    }

    private fun initWidgets(){
        initCancelButton()
        initConfirmButton()
        initPharmacyNameEditText()
        initAddressEditText()
        initSetAddressToCurrentLocationButton()
        initPickAddressFromMapButton()
        initTakePictureButton()
        initOpenGalleryButton()
        initCancelPhotoButton()
    }

    private fun initPickLocationFromMapLauncher(){
        pickLocationFromMapLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                val latitude = data?.getDoubleExtra("latitude", 0.0)
                val longitude = data?.getDoubleExtra("longitude", 0.0)

                if(latitude != null && longitude != null){
                    uponReceivingLocationFromMap(latitude, longitude)
                }
            }
        }
    }

    private fun initTakePictureButton(){
        takePictureButton = findViewById(R.id.btnTakePicture)
        takePictureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                // Permission is already granted, launch camera activity
                launchCamera()
            }
        }
    }

    private fun initOpenGalleryButton(){
        openGalleryButton = findViewById(R.id.btnOpenGallery)
        openGalleryButton.setOnClickListener {
            launchGallery()
        }
    }

    private fun initCancelPhotoButton(){
        this.cancelPhotoButton = findViewById<FloatingActionButton>(R.id.btnCancelPhoto)
        cancelPhotoButton.setOnClickListener {
            resetPhotoSection()
        }
    }

    private fun launchCamera(){
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        cameraLauncher.launch(intent)
    }

    private fun launchGallery(){
        val getIntent = Intent(Intent.ACTION_GET_CONTENT);
        getIntent.type = "image/*";

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.type = "image/*";

        val chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent));

        galleryLauncher.launch(chooserIntent);
    }

    private fun initCameraLauncher(){
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    uponReceivingImage(bitmap)
                }
            }
        }
    }

    private fun initGalleryLauncher(){
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val dataUri = result.data?.data
                if (dataUri != null) {
                    try {
                        // Convert the URI to a Bitmap
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, dataUri)
                        uponReceivingImage(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle the error, e.g., show a toast message
                    }
                } else {
                    // Handle the case where dataUri is null
                }
            }
        }
    }


    private fun showDialog(title : String, message : String, onYesClicked : () -> Unit, onNoClicked : () -> Unit){
        val builder = AlertDialog.Builder(this)
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

    private fun setLocationToCurrentLocation(){
        addPharmacyDtoBuilder.setLatitude(lastKnownLocation.latitude)
        addPharmacyDtoBuilder.setLongitude(lastKnownLocation.longitude)
        setLocationSectionFilled("Address set to current location.")
        Toast.makeText(this, "Address set to current location", Toast.LENGTH_SHORT).show()
    }

    private fun checkIfConfirmButtonShouldBeEnabled(){
        confirmButton.isEnabled = nameEditTextPopulated && locationPopulated
    }

    private fun setLocationSectionFilled(textForEditText : String){

        // Make the edit text not editable and change its background color
        addressEditText.setText(textForEditText)
        addressEditText.isEnabled = false
        addressEditText.backgroundTintList = resources.getColorStateList(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

        // Find the container for the buttons and EditText
        val addressContainer = findViewById<LinearLayout>(R.id.pharmacyLocationSection)


        addressContainer.removeView(setAddressToCurrentLocationButton)
        switchBehaviorOfPickAddressFromMapButton()

        locationPopulated = true
        checkIfConfirmButtonShouldBeEnabled()
    }

    private fun initPharmacyNameEditText(){
        pharmacyNameEditText = findViewById<EditText>(R.id.editTextPharmacyName)
        pharmacyNameEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                pharmacyNameEditText.error = null
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    nameEditTextPopulated = false
                    confirmButton.setEnabled(false)
                } else {
                    nameEditTextPopulated = true
                    checkIfConfirmButtonShouldBeEnabled()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    // Cancel button
    private fun initCancelButton(){
        cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            this.showDialog("Cancel", "Are you sure you want to cancel?", ::finish) { }
        }
    }

    // Confirm button
    private fun initConfirmButton(){
        confirmButton = findViewById<Button>(R.id.btnConfirm)
        confirmButton.setOnClickListener {
            val messageSb = StringBuilder()
            messageSb.append("Are you sure you want to add the pharmacy with the following details?\n\n")
            messageSb.append("Name: ${pharmacyNameEditText.text}\n")
            messageSb.append("Latitude: ${addPharmacyDtoBuilder.getLatitude()}\n")
            messageSb.append("Longitude: ${addPharmacyDtoBuilder.getLongitude()}\n")
            if(!picturePopulated){
                messageSb.append("Warning: No picture was added to the pharmacy.\n")
            }

            showDialog("Confirm Pharmacy Details", messageSb.toString(), ::addPharmacyAndFinish) {}

        }
        checkIfConfirmButtonShouldBeEnabled()
    }

    // Set address to current location button (1st button in location section
    private fun initSetAddressToCurrentLocationButton(){
        setAddressToCurrentLocationButton = findViewById<FloatingActionButton>(R.id.btnSetAddressToCurrentLocation)
        setAddressToCurrentLocationButton.setOnClickListener {
            showDialog("Pharmacy Location", "Set the location of the new pharmacy to the current location?", ::setLocationToCurrentLocation) {}
        }
    }

    // Pick address from map button (2nd button in location section)
    private fun initPickAddressFromMapButton(){
        pickAddressFromMapButton = findViewById<FloatingActionButton>(R.id.btnPickAddressFromMap)
        pickAddressFromMapButton.setOnClickListener {

            // Start the map activity to pick the address
            openMapFunction()
        }
    }

    private fun initAddressEditText(){
        addressEditText = findViewById<AutoCompleteTextView>(R.id.editTextAddress)
        val placesApi = PlaceAPI.Builder().apiKey(ConfigClass.getValueFromAndroidManifest(this, "com.google.android.geo.API_KEY")).build(this)
        placesApi.locationBiasLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
        placesApi.locationBiasRadius = 100000 // 100 km
        addressEditText.setAdapter(PlacesAutoCompleteAdapter(this, placesApi))

        addressEditText.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val place = parent.getItemAtPosition(position) as Place
                addressEditText.setText(place.description)
            }


        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    locationPopulated = false
                    confirmButton.setEnabled(false)
                } else {
                    locationPopulated = true
                    checkIfConfirmButtonShouldBeEnabled()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun openMapFunction(){
        //launch back the main menu activity, which will have the map
        val intent = Intent(this, PickLocationFromMapActivity::class.java)


        pickLocationFromMapLauncher.launch(intent)
    }

    private fun switchBehaviorOfPickAddressFromMapButton(){
        Log.i("DEBUG", "Switching behavior of pick address from map button")
        pickAddressFromMapButton.setImageResource(R.drawable.baseline_close_24)
        pickAddressFromMapButton.backgroundTintList = resources.getColorStateList(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
        pickAddressFromMapButton.setOnClickListener {

            // Clear the address EditText and update state
            addressEditText.text.clear()
            locationPopulated = false
            addressEditText.isEnabled = true
            addressEditText.backgroundTintList = resources.getColorStateList(R.color.white)

            // Add the current location button back (need to remove and add again to keep same order)
            val addressContainer = findViewById<LinearLayout>(R.id.pharmacyLocationSection)
            addressContainer.removeView(pickAddressFromMapButton)
            addressContainer.addView(setAddressToCurrentLocationButton)
            addressContainer.addView(pickAddressFromMapButton)

            pickAddressFromMapButton.setImageResource(R.drawable.map_icon)
            pickAddressFromMapButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)

            pickAddressFromMapButton.setOnClickListener {
                openMapFunction()
            }

            checkIfConfirmButtonShouldBeEnabled()
        }
        pickAddressFromMapButton.layoutParams = LinearLayout.LayoutParams(40.dpToPx(resources), 40.dpToPx(resources)).apply {
            marginStart = 8.dpToPx(resources)
            marginEnd = 8.dpToPx(resources)
        }



    }

    private fun uponReceivingLocationFromMap(latitude: Double, longitude: Double){

        // Add received coordinates to the AddPharmacyDto
        addPharmacyDtoBuilder.setLatitude(latitude)
        addPharmacyDtoBuilder.setLongitude(longitude)

        // Update the address EditText
        setLocationSectionFilled("Address set to custom location.")


        Toast.makeText(this, "Address set to picked location: $latitude, $longitude", Toast.LENGTH_SHORT).show()
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted, launch camera activity
                    launchCamera()
                } else {
                    // Camera permission denied, show an explanation or handle it accordingly
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uponReceivingImage(bitmap: Bitmap){

        //remove the two buttons for images
        takePictureButton.visibility = View.GONE
        openGalleryButton.visibility = View.GONE


        // Save the image in file system
        val picturePath = UtilFunctions.saveToInternalStorage(bitmap, "pharmacy_picture", this)

        // Add the path to the AddPharmacyDto
        addPharmacyDtoBuilder.setPicturePath(picturePath)

        // Add the picture extension to the AddPharmacyDto
        addPharmacyDtoBuilder.setPictureExtension(picturePath.substring(picturePath.lastIndexOf(".") + 1))

        picturePopulated = true

        //create image view and add it to the layout
        val imageViewPhoto = findViewById<ImageView>(R.id.imageViewPhoto)
        imageViewPhoto.setImageBitmap(bitmap)


        //get photo container relative layout
        val photoContainer = findViewById<RelativeLayout>(R.id.photoContainer)
        photoContainer.visibility = View.VISIBLE


    }

    // Function to reset the photo section to its original state
    private fun resetPhotoSection() {
        val imageViewPhoto = findViewById<ImageView>(R.id.imageViewPhoto)

        // Hide the image view and cancel button
        imageViewPhoto.setImageBitmap(null)
        findViewById<RelativeLayout>(R.id.photoContainer).visibility = View.GONE

        // Show the take picture and open gallery buttons
        takePictureButton.visibility = View.VISIBLE
        openGalleryButton.visibility = View.VISIBLE

        // Clear the AddPharmacyDto picture path and extension by unitializing them
        addPharmacyDtoBuilder.setPicturePath("")
        addPharmacyDtoBuilder.setPictureExtension("")
        picturePopulated = false
    }


    private fun addPharmacyAndFinish() {
        // Add name to DTO
        addPharmacyDtoBuilder.setName(pharmacyNameEditText.text.toString())
        if(addPharmacyDtoBuilder.getLatitude() == null || addPharmacyDtoBuilder.getLongitude() == null){
            addPharmacyDtoBuilder.setAddress(addressEditText.text.toString())
        }
        Log.i("DEBUG", addPharmacyDtoBuilder.toString())

        // Launch a coroutine to call the suspend function
        lifecycleScope.launch {
            try {
                pharmacyService.addPharmacy(addPharmacyDtoBuilder.build())

                runOnUiThread {
                        onAddPharmacySuccess()
                }

            } catch (exception: Exception) {
                Log.i("DEBUG", exception.stackTraceToString())
                val builder = AlertDialog.Builder(this@AddPharmacyActivity)
                builder.setTitle("Error")
                builder.setMessage(exception.message)

                builder.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    if(exception is PharmacyNameAlreadyInUse){
                        pharmacyNameEditText.error = "Pharmacy name already in use"
                    }
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun onAddPharmacySuccess() {
        // Clear all elements on the activity
        clearAllElements()

        setContentView(R.layout.activity_add_pharmacy_success)


        val handler = Handler()
        handler.postDelayed({ // Do something after 5s = 5000ms
            finish()
        }, TIME_SHOW_ADD_PHARMACY_SUCCESS)
    }

    private fun clearAllElements() {
        // Remove all views from the main layout
        val mainLayout = findViewById<RelativeLayout>(R.id.root_layout)
        mainLayout.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        // eliminate photo, if it exists
        try {

            if(addPharmacyDtoBuilder.getPicturePath() != null){
                val file = File(addPharmacyDtoBuilder.getPicturePath()!!)
                file.delete()
            }

        }catch (e: Exception){
            Log.i("DEBUG", e.stackTraceToString())
        }
    }





    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
        return when {
            SDK_INT >= 33 -> getParcelable(key, clazz)
            else -> @Suppress("DEPRECATION") getParcelable(key)
        }
    }


}
