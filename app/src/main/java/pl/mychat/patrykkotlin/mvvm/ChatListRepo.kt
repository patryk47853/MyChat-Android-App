package pl.mychat.patrykkotlin.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.modal.RecentChat

class ChatListRepo() {

    val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChat>> {

        val mainChatList = MutableLiveData<List<RecentChat>>()

        firestore.collection("Conversation${Utils.getUidLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    return@addSnapshotListener
                }

                val chatlist = mutableListOf<RecentChat>()

                snapshot?.forEach { document ->

                    val chatlistmodel = document.toObject(RecentChat::class.java)

                    if (chatlistmodel.sender.equals(Utils.getUidLoggedIn())) {
                        chatlistmodel.let {
                            chatlist.add(it)
                        }
                    }
                }

                mainChatList.value = chatlist
            }

        return mainChatList
    }
}