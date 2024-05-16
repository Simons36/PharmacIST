package pt.ulisboa.tecnico.cmov.pharmacist

import android.R.attr.button
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ulisboa.tecnico.cmov.pharmacist.dto.AddPharmacyDtoBuilder
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions.Companion.dpToPx


class AddPharmacyActivity() : AppCompatActivity() {

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
    private lateinit var addressEditText : EditText
    private lateinit var setAddressToCurrentLocationButton : FloatingActionButton
    private lateinit var pickAddressFromMapButton : FloatingActionButton

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pharmacy)

        val extras = intent.extras
        if(extras != null){
            lastKnownLocation = extras.getParcelableCompat("lastKnownLocation", Location::class.java)!!
        }else{
            Toast.makeText(this, "Location services not available right now", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Init buttons, edit texts, etc.
        initWidgets()



    }

    private fun initWidgets(){
        initCancelButton()
        initConfirmButton()
        initPharmacyNameEditText()
        initAddressEditText()
        initSetAddressToCurrentLocationButton()
        initPickAddressFromMapButton()
    }

    private fun showCurrentLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pharmacy Location")
        builder.setMessage("Set the location of the new pharmacy to the current location?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Set the address to the current location
            setLocationToCurrentLocation()

            locationPopulated = true
            checkIfConfirmButtonShouldBeEnabled()

            addressEditText.setText("Address set to current location.")
            addressEditText.isEnabled = false
            addressEditText.backgroundTintList = resources.getColorStateList(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

            removeTwoButtonsAndAddClearButtonForLocationSection()

            Toast.makeText(this, "Address set to current location", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setLocationToCurrentLocation(){
        addPharmacyDtoBuilder.setLatitude(lastKnownLocation.latitude)
        addPharmacyDtoBuilder.setLongitude(lastKnownLocation.longitude)
    }

    private fun checkIfConfirmButtonShouldBeEnabled(){
        confirmButton.isEnabled = nameEditTextPopulated && locationPopulated
    }

    private fun removeTwoButtonsAndAddClearButtonForLocationSection(){
        // Find the container for the buttons and EditText
        val addressContainer = findViewById<LinearLayout>(R.id.pharmacyLocationSection)


        addressContainer.removeView(setAddressToCurrentLocationButton)
        switchBehaviorOfPickAddressFromMapButton()


    }

    private fun initPharmacyNameEditText(){
        pharmacyNameEditText = findViewById<EditText>(R.id.editTextPharmacyName)
        pharmacyNameEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
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
            finish() // This will close the activity and go back to the previous one
        }
    }

    // Confirm button
    private fun initConfirmButton(){
        confirmButton = findViewById<Button>(R.id.btnConfirm)
        confirmButton.setOnClickListener {

        }
        checkIfConfirmButtonShouldBeEnabled()
    }

    // Set address to current location button (1st button in location section
    private fun initSetAddressToCurrentLocationButton(){
        setAddressToCurrentLocationButton = findViewById<FloatingActionButton>(R.id.btnSetAddressToCurrentLocation)
        setAddressToCurrentLocationButton.setOnClickListener {
            showCurrentLocationDialog()
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
        addressEditText = findViewById<EditText>(R.id.editTextAddress)
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
        val intent = Intent(this, MainMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("fromAddPharmacyActivity", true)
        startActivity(intent)
    }

    private fun switchBehaviorOfPickAddressFromMapButton(){
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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String, clazz: Class<T>): T? {
        return when {
            SDK_INT >= 33 -> getParcelable(key, clazz)
            else -> @Suppress("DEPRECATION") getParcelable(key)
        }
    }


}
