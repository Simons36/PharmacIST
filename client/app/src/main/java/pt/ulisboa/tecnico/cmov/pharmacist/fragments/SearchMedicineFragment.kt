package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.MedicineDetailsActivity
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter.MedicineAdapter
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.service.MedicineServiceImpl

class SearchMedicineFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var medicinesRecyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        searchView = view.findViewById(R.id.searchView)
        medicinesRecyclerView = view.findViewById(R.id.medicinesRecyclerView)

        // Set up RecyclerView
        medicinesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        medicineAdapter = MedicineAdapter(emptyList()) { medicine ->
            // Handle item click here
            val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
            // Pass relevant information about the clicked medicine to the details activity
            intent.putExtra("medicine_name", medicine.getName())
            startActivity(intent)
        }
        medicinesRecyclerView.adapter = medicineAdapter

        // Set up SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.length >= 3) fetchMedicines(it, requireContext())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length >= 3) {
                        fetchMedicines(it, requireContext())
                    } else {
                        medicineAdapter.updateMedicines(emptyList())
                    }
                }
                return false
            }
        })
    }

    private fun fetchMedicines(query: String, context: Context) {
        lifecycleScope.launch {
            try {
                val medicines: List<MedicineDTO> = MedicineServiceImpl.searchMedicines(query, context)
                medicineAdapter.updateMedicines(medicines)
            } catch (e: Exception) {
                // Handle error, e.g., show a toast or log the error
                e.printStackTrace()
            }
        }
    }
}
