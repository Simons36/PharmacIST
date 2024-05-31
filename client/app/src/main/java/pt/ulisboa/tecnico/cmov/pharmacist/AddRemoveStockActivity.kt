package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.inventory.service.InventoryServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter.MedicineInventoryAdapter
import kotlin.properties.Delegates

class AddRemoveStockActivity : AppCompatActivity(){

    private lateinit var quantityEditText: EditText
    private var selectedRadioButton: Int = R.id.radioAdd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_remove_stock)

        val pharmacyName = intent.getStringExtra("pharmacyName")

        val spinner: Spinner = findViewById(R.id.spinner1)
        lifecycleScope.launch {
            val inventory =
                InventoryServiceImpl.getPharmacyInventory(pharmacyName!!, this@AddRemoveStockActivity)

            // add iventory to spinner
            val medicineNamesFromInventory = inventory.map { it.name }
            val adapter = ArrayAdapter(this@AddRemoveStockActivity, android.R.layout.simple_spinner_item, medicineNamesFromInventory)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // init quantity edit text
        quantityEditText = findViewById<EditText>(R.id.editTextQuantity)

        //init radio buttons add and remove
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedRadioButton = checkedId
        }



        // init btnConfirm
        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val medicineName = spinner.selectedItem.toString()
            val quantity = quantityEditText.text.toString().toInt()


            if(selectedRadioButton == -1){
                // show error message
            }else if(selectedRadioButton == R.id.radioAdd){
                // add quantity to inventory
                lifecycleScope.launch {
                    InventoryServiceImpl.addStock(pharmacyName!!, medicineName, quantity, this@AddRemoveStockActivity)
                }
            }else if(selectedRadioButton == R.id.radioRemove){
                // remove quantity from inventory
                lifecycleScope.launch {
                    InventoryServiceImpl.removeStock(pharmacyName!!, medicineName, quantity, this@AddRemoveStockActivity)
                }
            }
            finish()
        }


    }

}