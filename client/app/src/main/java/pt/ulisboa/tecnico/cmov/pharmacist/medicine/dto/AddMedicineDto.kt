package pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddMedicineDto(
    val name : String,
    val pharmacyName : String,
    val quantity : Int,
    val purpose : String,
    val photoPath : String
)