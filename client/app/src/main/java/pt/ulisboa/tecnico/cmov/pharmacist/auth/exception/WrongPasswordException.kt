package pt.ulisboa.tecnico.cmov.pharmacist.auth.exception

class WrongPasswordException(email: String) : Exception("Wrong password!") {

    // Declare email as a property
    private val email: String

    init {
        // Initialize the email property with the value received in the constructor
        this.email = email
    }

    // Getter method for the email property
    fun getEmail(): String {
        return email
    }
}
