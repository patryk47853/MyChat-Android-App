@file:Suppress("DEPRECATION")

package pl.mychat.patrykkotlin.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.patrykkotlin.mychat.R
import pl.patrykkotlin.mychat.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)

        binding.signUpTextToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {
            userName = binding.signUpEtName.text.toString()
            userEmail = binding.signUpEmail.text.toString()
            userPassword = binding.signUpPassword.text.toString()

            if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                createAnAccount(userName, userPassword, userEmail)
            }
        }
    }

    private fun createAnAccount(name: String, password: String, email: String) {
        progressDialog.show()
        progressDialog.setMessage("Registering user...")

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val dataHashMap = hashMapOf(
                    "userid" to (user?.uid ?: ""),
                    "username" to name,
                    "useremail" to email,
                    "status" to "default",
                    "imageUrl" to "https://icons.iconarchive.com/icons/papirus-team/papirus-status/512/avatar-default-icon.png"
                )

                firestore.collection("User").document(user?.uid ?: "").set(dataHashMap)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(
                            this, "Registration process failed: ${e.message}", Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Registration process failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}