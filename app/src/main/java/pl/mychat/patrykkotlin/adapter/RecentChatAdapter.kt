package pl.mychat.patrykkotlin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import pl.mychat.patrykkotlin.modal.RecentChat
import pl.patrykkotlin.mychat.R

class RecentChatAdapter : RecyclerView.Adapter<MyChatListHolder>() {

    private var listOfChats = listOf<RecentChat>()
    private var listener: OnChatClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)
        return MyChatListHolder(view)
    }

    override fun getItemCount(): Int = listOfChats.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<RecentChat>) {
        this.listOfChats = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {
        val chatList = listOfChats[position]

        holder.bind(chatList)

        holder.itemView.setOnClickListener {
            listener?.onChatClickedItem(position, chatList)
        }
    }

    fun setOnChatClickListener(listener: OnChatClicked) {
        this.listener = listener
    }
}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    private val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    private val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    private val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)

    fun bind(chatList: RecentChat) {
        userName.text = chatList.name

        val theMessage = chatList.message?.split(" ")?.take(4)?.joinToString(" ") ?: ""
        val makeLastMessage = "${chatList.person}: $theMessage"
        lastMessage.text = makeLastMessage

        Glide.with(itemView.context).load(chatList.friendsimage).into(imageView)

        timeView.text = chatList.time?.substring(0, 5) ?: ""
    }
}

interface OnChatClicked {
    fun onChatClickedItem(position: Int, chatList: RecentChat)
}