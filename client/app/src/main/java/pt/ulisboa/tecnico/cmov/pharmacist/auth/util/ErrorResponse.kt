package pt.ulisboa.tecnico.cmov.pharmacist.auth.util

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val field: String?
)
