package pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MedicineDTO(
    private val name: String,
    @SerialName("photoPath")
    private val _photoPath: String? = null // Make photoPath optional with a default value of null
) {
    fun getName(): String {
        return name
    }

    fun getPhotoPath(): String? {
        return _photoPath
    }
}
