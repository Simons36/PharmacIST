package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response.UpdatePharmaciesStatusResponse

// For all operations with the server related to pharmacies
interface ParmacyService {

    suspend fun addPharmacy(pharmacy : AddPharmacyDto, context : Context)

    suspend fun syncPharmacyInfo(knownVersion : Int, context: Context) : UpdatePharmaciesStatusResponse

}