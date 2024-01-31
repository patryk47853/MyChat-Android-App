package pl.mychat.patrykkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.modal.Message
import pl.patrykkotlin.mychat.R

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }

    private var listOfMessage = listOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutId = if (viewType == RIGHT) R.layout.chatitemright else R.layout.chatitemleft
        val view = inflater.inflate(layoutId, parent, false)
        return MessageHolder(view)
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.bind(message)
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == Utils.getUidLoggedIn()) RIGHT else LEFT

    fun setList(newList: List<Message>) {
        this.listOfMessage = newList
    }
}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageText: TextView = itemView.findViewById(R.id.show_message)
    private val timeOfSent: TextView = itemView.findViewById(R.id.timeView)

    fun bind(message: Message) {
        messageText.visibility = View.VISIBLE
        timeOfSent.visibility = View.VISIBLE

        messageText.text = message.message
        timeOfSent.text = message.time?.substring(0, 5) ?: ""
    }
}
