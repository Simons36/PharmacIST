package pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto

import kotlinx.serialization.Serializable

@Serializable
data class OtherMedicineDto(
    val name : String,
    val quantity : Int,
    val purpose : String,
)