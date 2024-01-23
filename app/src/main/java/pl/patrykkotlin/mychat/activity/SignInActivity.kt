@file:Suppress("DEPRECATION")

package pl.patrykkotlin.mychat.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        progressDialog = ProgressDialog(this)

        binding.signInTextToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            userEmail = binding.loginetemail.text.toString()
            userPassword = binding.loginetpassword.text.toString()

            if (userEmail.isEmpty()) {
                showToast("E-mail:")
                return@setOnClickListener
            }

            if (userPassword.isEmpty()) {
                showToast("Password:")
                return@setOnClickListener
            }

            signIn(userPassword, userEmail)
        }
    }

    private fun signIn(password: String, email: String) {
        progressDialog.show()
        progressDialog.setMessage("Signing in...")

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            progressDialog.dismiss()
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                handleSignInFailure(it.exception)
            }
        }.addOnFailureListener {
            handleSignInFailure(it)
        }
    }

    private fun handleSignInFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                showToast("Invalid credentials - please try again")
            }
            else -> {
                showToast("Authentication failed - please try againid")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        progressDialog.dismiss()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }
}