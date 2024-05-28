package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.service

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

interface PharmacyInfoDbService {

    fun getCachedPharmaciesInfo(context : Context) : List<PharmacyDto>

    fun removePharmacyInfoFromCache(pharmacy : PharmacyDto, context : Context)

    fun addPharmacyInfoToCache(pharmacy : PharmacyDto, context : Context)

}