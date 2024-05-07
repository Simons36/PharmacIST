package pt.ulisboa.tecnico.cmov.pharmacist.auth.exception

class AccountNotFoundException(email : String) : Exception("No account found with email $email"){

    private val email : String;

    init {
        this.email = email;
    }

    // Getter method for the email property
    fun getEmail(): String {
        return email
    }

}