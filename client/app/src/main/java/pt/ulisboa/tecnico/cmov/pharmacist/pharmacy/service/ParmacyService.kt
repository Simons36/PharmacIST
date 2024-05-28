package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

// For all operations with the server related to pharmacies
interface ParmacyService {

    suspend fun addPharmacy(pharmacy : AddPharmacyDto, context : Context)

    suspend fun updatePharmacyInfo(pharmaciesList : List<PharmacyDto>, context: Context) : Pair<List<PharmacyDto>, List<PharmacyDto>>

}