package pt.ulisboa.tecnico.cmov.pharmacist.auth

import android.content.Context

interface AuthenticationService {

    fun login(email : String, password : String, callback : (Boolean, String?) -> Unit);

    fun register(username : String, email : String, password: String);

    fun logout();

}