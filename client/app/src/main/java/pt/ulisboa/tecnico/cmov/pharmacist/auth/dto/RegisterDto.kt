package pt.ulisboa.tecnico.cmov.pharmacist.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val username: String,
    val email: String,
    val password: String
)