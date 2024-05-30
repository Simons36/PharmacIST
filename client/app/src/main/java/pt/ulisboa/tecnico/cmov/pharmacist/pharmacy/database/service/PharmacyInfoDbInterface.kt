package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.service

import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

/**
 * API for the cache database that will store information about pharmacies
 */
interface PharmacyInfoDbInterface {

    fun getCachedPharmaciesInfo() : List<PharmacyDto>

    fun removePharmacyInfoFromCache(pharmacy : PharmacyDto)

    fun addPharmacyInfoToCache(pharmacy : PharmacyDto)

    fun getLatestVersion() : Int

    fun setLatestVersion(version : Int)

}