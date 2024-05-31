package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto

import kotlinx.serialization.Serializable

@Serializable
data class PharmacyDto(
    val name : String,
    val address : String?,
    val latitude : Double,
    val longitude : Double,
    var isFavorite : Boolean? = false
)
