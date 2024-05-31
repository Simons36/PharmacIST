package pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicineDTO(private val name: String) {
    fun getName(): String {
        return name
    }
}
