package pt.ulisboa.tecnico.cmov.pharmacist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ulisboa.tecnico.cmov.pharmacist.R

class MedicineDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_details)

        // Retrieve the medicine name from the intent
        val medicineName = intent.getStringExtra("medicine_name")

        // Initialize the TextView
        val medicineNameTextView: TextView = findViewById(R.id.medicineNameTextView)

        // Set the medicine name in the TextView
        medicineName?.let {
            medicineNameTextView.text = it
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            // Return to the previous activity (main menu)
            onBackPressed()
        }
    }
}
