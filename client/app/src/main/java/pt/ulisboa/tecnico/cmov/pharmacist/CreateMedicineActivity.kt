package pt.ulisboa.tecnico.cmov.pharmacist

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.AddMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import java.io.IOException

class CreateMedicineActivity : AppCompatActivity(){

    private val CAMERA_PERMISSION_CODE = 1;

    private var pharmacyName: String? = null

    private val medicineService = MedicineServiceImpl()

    private lateinit var nameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var purposeEditText: EditText
    private lateinit var takePictureButton : Button
    private lateinit var pickFromGalleryButton: Button
    private lateinit var cancelButton: Button
    private lateinit var addNewMedicineButton : Button
    private lateinit var cancelPhotoButton : FloatingActionButton

    private var imagePath : String? = null

    // To launch camera and retrieve image
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    // To pick image from gallery
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get extra data from the intent
        pharmacyName = intent.getStringExtra("pharmacyName")
        val creationMethod = intent.getStringExtra("creationMethod")

        if(creationMethod == "parameters"){
            setContentView(R.layout.activity_create_medicine)

            initNameEditText()
            initQuantityEditText()
            initPurposeEditText()
            initAddMedicineButton()
            initCancelButton()
            initTakePictureButton()
            initPickFromGalleryButton()
            initCancelPhotoButton()


            // These are for receiving
            initCameraLauncher()
            initGalleryLauncher()

        }
    }

    private fun initNameEditText(){
        nameEditText = findViewById(R.id.editTextMedicineName)
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkIfAddButtonShouldBeEnabled()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun initQuantityEditText(){
        quantityEditText = findViewById(R.id.editTextQuantity)
        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkIfAddButtonShouldBeEnabled()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun initPurposeEditText(){
        purposeEditText = findViewById(R.id.editTextPurpose)
        purposeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkIfAddButtonShouldBeEnabled()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun initAddMedicineButton(){
        addNewMedicineButton = findViewById(R.id.btnAddMedicine)
        checkIfAddButtonShouldBeEnabled()
        addNewMedicineButton.setOnClickListener {
            lifecycleScope.launch {
                val name = nameEditText.text.toString()
                val quantity = quantityEditText.text.toString().toInt()
                val purpose = purposeEditText.text.toString()

                val medicine = AddMedicineDto(name, pharmacyName!!, quantity, purpose, imagePath!!)

                try {
                    medicineService.addMedicine(medicine, this@CreateMedicineActivity)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@CreateMedicineActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initCancelButton(){
        cancelButton = findViewById(R.id.btnCancel)
        cancelButton.setOnClickListener {
            UtilFunctions.showDialog("Cancel", "Are you sure you want to cancel?", this, ::finish) {}
        }
    }



    private fun checkIfAddButtonShouldBeEnabled(){
        addNewMedicineButton.isEnabled = nameEditText.text.isNotEmpty() && quantityEditText.text.isNotEmpty() && purposeEditText.text.isNotEmpty() && imagePath != null
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

    private fun initPickFromGalleryButton(){
        this.pickFromGalleryButton = findViewById(R.id.btnOpenGallery)
        pickFromGalleryButton.setOnClickListener {
            launchGallery()
        }
    }

    private fun initCancelPhotoButton(){
        this.cancelPhotoButton = findViewById<FloatingActionButton>(R.id.btnCancelPhoto)
        cancelPhotoButton.setOnClickListener {
            resetPhotoSection()
        }
    }

    private fun resetPhotoSection() {
        val imageViewPhoto = findViewById<ImageView>(R.id.imageViewPhoto)

        // Hide the image view and cancel button
        imageViewPhoto.setImageBitmap(null)
        findViewById<RelativeLayout>(R.id.photoContainer).visibility = View.GONE

        // Show the take picture and open gallery buttons
        takePictureButton.visibility = View.VISIBLE
        pickFromGalleryButton.visibility = View.VISIBLE

        // Clear the AddPharmacyDto picture path and extension by unitializing them
        imagePath = null
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

    private fun uponReceivingImage(bitmap: Bitmap){

        //remove the two buttons for images
        takePictureButton.visibility = View.GONE
        pickFromGalleryButton.visibility = View.GONE


        // Save the image in file system
        imagePath = UtilFunctions.saveToInternalStorage(bitmap, "new_medicine_picture", this)


        //create image view and add it to the layout
        val imageViewPhoto = findViewById<ImageView>(R.id.imageViewPhoto)
        imageViewPhoto.setImageBitmap(bitmap)


        //get photo container relative layout
        val photoContainer = findViewById<RelativeLayout>(R.id.photoContainer)
        photoContainer.visibility = View.VISIBLE

        checkIfAddButtonShouldBeEnabled()
    }
}