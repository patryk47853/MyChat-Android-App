package pl.mychat.patrykkotlin.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.modal.Message

class MessageRepo {

    val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendid: String): LiveData<List<Message>> {
        val message = MutableLiveData<List<Message>>()

        val uniqueId = listOf(Utils.getUidLoggedIn(), friendid).sorted()
        val uniqueIdString = uniqueId.joinToString(separator = "")

        firestore.collection("Message").document(uniqueIdString).collection("chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    // Handle the exception, e.g., log an error
                    Log.e("MessageRepo", "Error getting messages: $exception")
                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Message>()

                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Message::class.java)

                        // Check if the messageModel is not null before adding to the list
                        if (messageModel != null) {
                            messageList.add(messageModel)
                        }
                    }

                    message.value = messageList
                }
            }

        return message
    }
}