package pt.ulisboa.tecnico.cmov.pharmacist.inventory.service

import kotlinx.serialization.Serializable

@Serializable
data class TempDto(
    val pharmacyName : String,
    val medicineName : String,
    val quantity : Int
)