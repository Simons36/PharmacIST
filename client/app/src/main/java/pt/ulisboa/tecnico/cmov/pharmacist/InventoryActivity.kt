package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.inventory.service.InventoryServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter.MedicineAdapter
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter.MedicineInventoryAdapter
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.DisplayMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl

class InventoryActivity : AppCompatActivity() {

    private lateinit var medicineRecyclerView: RecyclerView

    private lateinit var medicineService: MedicineServiceImpl
    private lateinit var medicineAdapter: MedicineAdapter

    private var pharmacyName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        pharmacyName = intent.getStringExtra("pharmacyName")

        medicineRecyclerView = findViewById(R.id.medicineInventoryRecyclerView)

        medicineRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val inventory =
                InventoryServiceImpl.getPharmacyInventory(pharmacyName!!, this@InventoryActivity)
            val adapter = MedicineInventoryAdapter(inventory) { medicine ->

            }

            medicineRecyclerView.adapter = adapter
        }
    }


}