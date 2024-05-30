package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response

import kotlinx.serialization.Serializable
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

@Serializable
data class UpdatePharmaciesStatusResponse(
    val remove : List<PharmacyDto>,
    val add : List<PharmacyDto>,
    val version : Int
){
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Add: \n")
        for(pharmacy in add){
            sb.append(pharmacy.toString())
            sb.append("\n")
        }
        sb.append("Remove: \n")
        for(pharmacy in remove){
            sb.append(pharmacy.toString())
            sb.append("\n")
        }
        sb.append("Version: $version")
        return sb.toString()
    }
}

