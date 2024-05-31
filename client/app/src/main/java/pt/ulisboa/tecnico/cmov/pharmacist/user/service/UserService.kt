package pt.ulisboa.tecnico.cmov.pharmacist.user.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO

interface UserService {

    suspend fun getFavorites(context: Context): List<String>

}