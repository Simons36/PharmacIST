package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response.UpdatePharmaciesStatusResponse

// For all operations with the server related to pharmacies
interface ParmacyService {

    suspend fun addPharmacy(pharmacy : AddPharmacyDto, context : Context)

    suspend fun syncPharmacyInfo(knownVersion : Int, context: Context) : UpdatePharmaciesStatusResponse

    suspend fun getPharmacyPhoto(pharmacyName : String, context: Context) : String

    suspend fun getPharmacyByName(pharmacyName : String, context: Context) : PharmacyDto

    suspend fun addFavoritePharmacy(pharmacyName : String, context: Context)

    suspend fun removeFavoritePharmacy(pharmacyName : String, context: Context)

}