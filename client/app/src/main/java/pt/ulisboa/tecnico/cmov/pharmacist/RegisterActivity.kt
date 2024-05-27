package pt.ulisboa.tecnico.cmov.pharmacist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.auth.AuthenticationServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.auth.dto.RegisterDto
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.EmailAlreadyExistsException
import pt.ulisboa.tecnico.cmov.pharmacist.auth.exception.UsernameAlreadyExistsException

class RegisterActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            // Handle register button click
            // Validate email, username, password and confirm password, then proceed with registration

            val email = editTextEmail.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            // Validate email, username, password and confirm password
            if (email.isEmpty()) {
                editTextEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                editTextUsername.error = "Username is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password is required"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                editTextConfirmPassword.error = "Confirm Password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                editTextConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Proceed with registration
            // Call the register method from the auth instance
            // Handle the success and error callbacks

            // Create register dto
            val registerDto = RegisterDto(username, email, password)

            lifecycleScope.launch {

                try {
                    AuthenticationServiceImpl.register(registerDto, applicationContext)
                    Toast.makeText(applicationContext, "User registration successful!", Toast.LENGTH_SHORT).show()
                    finish()
                }catch (e: EmailAlreadyExistsException){
                    editTextEmail.error = "Account with this email already exists"
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                } catch(e: UsernameAlreadyExistsException){
                    editTextUsername.error = "Account with this username already exists"
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                } catch(e: Exception){
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}