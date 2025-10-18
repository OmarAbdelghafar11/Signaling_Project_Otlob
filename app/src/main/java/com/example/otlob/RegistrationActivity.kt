package com.example.otlob

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Just set up toolbar title (no navigation icon)


        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val signUpButton = findViewById<Button>(R.id.buttonSignUp)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            when {
                name.isEmpty() || phone.isEmpty() || email.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Initialize Firebase Auth
                    val auth = FirebaseAuth.getInstance()
                    val database = FirebaseDatabase.getInstance().reference

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid

                                if (userId != null) {
                                    val user = mapOf(
                                        "name" to name,
                                        "phone" to phone,
                                        "email" to email
                                    )

                                    // Save user data in Realtime Database
                                    database.child("users").child(userId).setValue(user)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Registered successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish() // Go back to sign-in page
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Failed to save user data: ${dbTask.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}
