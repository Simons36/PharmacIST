package pt.ulisboa.tecnico.cmov.pharmacist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ulisboa.tecnico.cmov.pharmacist.auth.AuthenticationServiceImpl
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: AuthenticationServiceImpl // Declare a property to hold the Auth instance

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

            // Class to handle authentication operations (login, register, logout)
            auth = AuthenticationServiceImpl(applicationContext);


            auth.login(email, password) { success, errorMessage ->
                if (success) {
                    // Login successful, navigate to MainMenuActivity
                    val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: Finish LoginActivity to prevent back navigation
                } else {
                    // Login failed, show error message to the user
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            this@LoginActivity,
                            errorMessage ?: "Login failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }
    }
}

