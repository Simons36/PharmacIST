package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response

import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

data class UpdatePharmaciesStatusResponse(
    val remove : List<PharmacyDto>,
    val add : List<PharmacyDto>
)