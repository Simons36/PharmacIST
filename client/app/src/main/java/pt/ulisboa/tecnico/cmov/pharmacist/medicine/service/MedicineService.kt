package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO

interface MedicineService {

    // Method to fetch medicines based on a search query
    suspend fun searchMedicines(query: String, context: Context): List<MedicineDTO>

    suspend fun getAllMedicines(context: Context): List<MedicineDTO>
}