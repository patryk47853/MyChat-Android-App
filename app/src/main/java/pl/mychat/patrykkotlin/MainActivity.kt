package pl.mychat.patrykkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import pl.patrykkotlin.mychat.R

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        generateToken()
    }

    fun generateToken() {
        val firebaseInstance = FirebaseInstallations.getInstance()

        firebaseInstance.id.addOnSuccessListener { installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener { gettocken ->
                token = gettocken
                val hasHamp = hashMapOf<String, Any>("token" to token)
                firestore.collection("Tokens")
                    .document(pl.mychat.patrykkotlin.Utils.Companion.getUidLoggedIn()).set(hasHamp)
                    .addOnSuccessListener {
                    }
            }
        }.addOnFailureListener {
        }
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser != null) {
            firestore.collection("User")
                .document(pl.mychat.patrykkotlin.Utils.Companion.getUidLoggedIn())
                .update("status", "Last seen in: Wroclaw, Poland")
        }
    }

    override fun onPause() {
        super.onPause()

        if (auth.currentUser != null) {
            firestore.collection("User")
                .document(pl.mychat.patrykkotlin.Utils.Companion.getUidLoggedIn())
                .update("status", "Offline")
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            firestore.collection("User")
                .document(pl.mychat.patrykkotlin.Utils.Companion.getUidLoggedIn())
                .update("status", "Last seen in: Wroclaw, Poland")

        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            // If we are on the Home fragment, exit the app
            if (navController.currentDestination?.id == R.id.homeFragment) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }
}







