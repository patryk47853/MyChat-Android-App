package pl.mychat.patrykkotlin.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import pl.mychat.patrykkotlin.MyApplication
import pl.mychat.patrykkotlin.SharedPrefs
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.modal.Message
import pl.mychat.patrykkotlin.modal.RecentChat
import pl.mychat.patrykkotlin.modal.User
import pl.mychat.patrykkotlin.notification.entity.NotificationData
import pl.mychat.patrykkotlin.notification.entity.PushNotification
import pl.mychat.patrykkotlin.notification.entity.Token
import pl.mychat.patrykkotlin.notification.network.RetrofitInstance

class ChatAppViewModel : ViewModel() {

    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()

    val userRepo = UserRepo()
    val messageRepo = MessageRepo()
    var token: String? = null
    val chatlistRepo = ChatListRepo()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        getCurrentUser()
        getRecentUsers()
    }

    fun getUsers(): LiveData<List<User>> {
        return userRepo.getUsers()
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext

            val hashMap = hashMapOf(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            val uniqueIdString = uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueIdString)
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Message").document(uniqueIdString).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->

                    val setHashMap = hashMapOf(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(setHashMap)

                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->

                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                token = tokenObject?.token!!

                                val loggedInUsername =
                                    mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(loggedInUsername, message.value!!), token!!
                                    ).also {
                                        sendNotification(it)
                                    }
                                } else {
                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                                }
                            }

                            Log.e("ViewModel", token.toString())

                            if (taskmessage.isSuccessful) {
                                message.value = ""
                            }
                        }
                }
        }

    fun getMessages(friend: String): LiveData<List<Message>> {
        return messageRepo.getMessages(friend)
    }

    fun getRecentUsers(): LiveData<List<RecentChat>> {
        return chatlistRepo.getAllChatList()
    }

    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
        }
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext

        firestore.collection("User").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->

                if (value != null && value.exists()) {

                    val user = value.toObject(User::class.java)
                    if (user != null) {
                        name.value = user.username!!
                        imageUrl.value = user.imageUrl!!

                        val mysharedPrefs = SharedPrefs(context)
                        mysharedPrefs.setValue("username", user.username!!)
                    }
                }
            }
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("User").document(Utils.getUidLoggedIn()).update(hashMapUser).addOnCompleteListener {

            if (it.isSuccessful){
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT ).show()
            }
        }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!)

        firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn()).update(hashMapUpdate)

        firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid!!).update("person", "you")
    }
}