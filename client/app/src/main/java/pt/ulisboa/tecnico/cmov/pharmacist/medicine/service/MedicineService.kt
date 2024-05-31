package pt.ulisboa.tecnico.cmov.pharmacist.medicine.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.AddMedicineDto
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO

interface MedicineService {

    // Method to fetch medicines based on a search query
    suspend fun searchMedicines(query: String, context: Context): List<MedicineDTO>

    suspend fun getAllMedicines(context: Context): List<MedicineDTO>

    suspend fun getMedicinePhoto(medicineName: String, context: Context): ByteArray

    suspend fun addMedicine(medicine: AddMedicineDto, context: Context)
}