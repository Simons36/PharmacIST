package pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto

import kotlinx.serialization.Serializable

@Serializable
data class DisplayMedicineDto(
    val name : String,
    val quantity : Int
)