package pl.mychat.patrykkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import pl.mychat.patrykkotlin.modal.User
import pl.patrykkotlin.mychat.R

class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<User>()
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int = listOfUsers.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = listOfUsers[position]

        val name = user.username?.split("\\s".toRegex())?.get(0) ?: ""
        holder.profileName.text = name

        if (user.status == "Online") {
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)
        } else {
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context).load(user.imageUrl).into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, user)
        }
    }

    fun setList(list: List<User>) {
        listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile: CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
}

interface OnItemClickListener {
    fun onUserSelected(position: Int, user: User)
}