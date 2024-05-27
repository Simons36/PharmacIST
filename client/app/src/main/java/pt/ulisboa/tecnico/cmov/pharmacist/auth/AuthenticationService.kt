package pt.ulisboa.tecnico.cmov.pharmacist.auth

import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.auth.dto.RegisterDto

interface AuthenticationService {

    fun login(email : String, password : String, context: Context, callback : (Boolean, String?) -> Unit);

    suspend fun register(registerDto: RegisterDto, context: Context);

    fun logout();

}