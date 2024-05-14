package pt.ulisboa.tecnico.cmov.pharmacist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddPharmacyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pharmacy)

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            finish() // This will close the activity and go back to the previous one
        }

        val pharmacyNameEditText = findViewById<EditText>(R.id.editTextPharmacyName)

        // Optionally, you can handle the text input here
        // Example: showing a toast message with the entered name when the "Cancel" button is clicked
        cancelButton.setOnClickListener {
            finish()
        }

        // Set Address to Current Location button functionality
        findViewById<FloatingActionButton>(R.id.btnSetAddressToCurrentLocation).setOnClickListener {
            showCurrentLocationDialog()
        }
    }

    private fun showCurrentLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Current Location")
        builder.setMessage("Set the address to the current location?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Set the address to the current location (you need to implement this logic)
            Toast.makeText(this, "Address set to current location", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
