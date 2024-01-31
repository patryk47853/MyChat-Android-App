package pl.mychat.patrykkotlin.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.modal.User

class UserRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<User>> {
        val users = MutableLiveData<List<User>>()

        firestore.collection("User").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            val userList = mutableListOf<User>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(User::class.java)

                if (user!!.userid != Utils.getUidLoggedIn()) {
                    user.let {
                        userList.add(it!!)
                    }
                }
            }

            users.value = userList
        }

        return users
    }
}
