package pt.ulisboa.tecnico.cmov.pharmacist

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl
import java.io.File

class MedicineDetailsActivity : AppCompatActivity() {

    private lateinit var medicinePhotoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_details)

        // Retrieve the medicine name from the intent
        val medicineName = intent.getStringExtra("medicine_name")

        // Initialize the TextView
        val medicineNameTextView: TextView = findViewById(R.id.medicineNameTextView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        medicinePhotoImageView = findViewById(R.id.medicineImageView)

        // Set the medicine name in the TextView
        medicineName?.let {
            medicineNameTextView.text = it
            nameTextView.text = it
            fetchAndDisplayMedicinePhoto(it)
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            // Return to the previous activity (main menu)
            onBackPressed()
        }
    }

    private fun fetchAndDisplayMedicinePhoto(medicineName: String) {
        lifecycleScope.launch {
            val medicineService = MedicineServiceImpl() // Create an instance of MedicineServiceImpl
            val photoJob: Deferred<ByteArray?> = async {
                try {
                    medicineService.getMedicinePhoto(medicineName, applicationContext) // Call the method on the instance
                } catch (e: Exception) {
                    Log.e("DEBUG", e.toString())
                    null
                }
            }

            val photoByteArray = photoJob.await()

            if (photoByteArray != null) {
                // Load photo from byte array
                medicinePhotoImageView.setImageBitmap(BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size))
            } else {
                Toast.makeText(applicationContext, "Failed to load medicine photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
