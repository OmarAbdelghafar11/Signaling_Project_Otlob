package com.example.otlob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nameEditText = findViewById(R.id.editTextName)
        phoneEditText = findViewById(R.id.editTextPhone)
        emailEditText = findViewById(R.id.editTextEmail)
        saveButton = findViewById(R.id.buttonSave)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Fetch user data from Firebase
            database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                val name = snapshot.child("name").value?.toString() ?: ""
                val phone = snapshot.child("phone").value?.toString() ?: ""
                val email = snapshot.child("email").value?.toString() ?: ""

                nameEditText.setText(name)
                phoneEditText.setText(phone)
                emailEditText.setText(email)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }

            saveButton.setOnClickListener {
                val newName = nameEditText.text.toString().trim()
                val newPhone = phoneEditText.text.toString().trim()

                if (newName.isEmpty() || newPhone.isEmpty()) {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    val updates = mapOf(
                        "name" to newName,
                        "phone" to newPhone
                    )
                    database.child("users").child(userId).updateChildren(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }


        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }



    }
}
