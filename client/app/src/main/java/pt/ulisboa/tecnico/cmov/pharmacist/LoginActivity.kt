package pt.ulisboa.tecnico.cmov.pharmacist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ulisboa.tecnico.cmov.pharmacist.auth.AuthenticationServiceImpl


class LoginActivity : AppCompatActivity() {

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



            AuthenticationServiceImpl.login(email, password, applicationContext) { success, errorMessage ->
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

        // Setup register button click listener
        val registerTextView = findViewById<TextView>(R.id.textViewGoToRegister)

        registerTextView.setOnClickListener {
            // Handle register button click
            // Navigate to RegisterActivity
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

