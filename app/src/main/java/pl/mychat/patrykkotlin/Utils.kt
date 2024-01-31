package pl.mychat.patrykkotlin

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Utils {


    companion object {
        @SuppressLint("StaticFieldLeak")
        val context = MyApplication.instance.applicationContext

        private val auth = FirebaseAuth.getInstance()
        private var userid: String = ""
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2

        fun getUidLoggedIn(): String {
            if (auth.currentUser != null) userid = auth.currentUser!!.uid

            return userid
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(): String {
            val formatter = SimpleDateFormat("HH:mm:ss")
            val date = Date(System.currentTimeMillis())
            val stringdate = formatter.format(date)

            return stringdate
        }
    }
}