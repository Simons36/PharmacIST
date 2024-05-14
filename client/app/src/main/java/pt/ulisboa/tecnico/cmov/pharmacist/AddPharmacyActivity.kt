package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class AddPharmacyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pharmacy)
        supportActionBar?.title = "Add Pharmacy"

        val cancelButton = findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {
            finish() // This will close the activity and go back to the previous one
        }
    }
}
