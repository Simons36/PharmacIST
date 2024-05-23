package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy

import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.AddPharmacyDto

// For all operations with the server related to pharmacies
interface ParmacyService {

    suspend fun addPharmacy(pharmacy : AddPharmacyDto)

}