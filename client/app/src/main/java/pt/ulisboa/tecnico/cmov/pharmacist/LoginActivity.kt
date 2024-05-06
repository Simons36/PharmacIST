package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import pt.ulisboa.tecnico.cmov.pharmacist.auth.Auth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: Auth // Declare a property to hold the Auth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            // Handle login button click
            // Validate username/email and password, then proceed with authentication

            val email = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString()

            // Validate username/email and password
            if (email.isEmpty()) {
                editTextUsername.error = "Username/Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }

            auth = Auth();

            //try {
                auth.login(applicationContext, email, password);
            //}catch (){
            //
            //}


        }
    }
}

