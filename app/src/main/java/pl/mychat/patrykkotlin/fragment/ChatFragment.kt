package pl.mychat.patrykkotlin.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.patrkkoltin.fragments.ChatFragmentArgs
import de.hdodenhof.circleimageview.CircleImageView
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.adapter.MessageAdapter
import pl.mychat.patrykkotlin.modal.Message
import pl.mychat.patrykkotlin.mvvm.ChatAppViewModel
import pl.patrykkotlin.mychat.R
import pl.patrykkotlin.mychat.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var adapter: MessageAdapter
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val textViewStatus = view.findViewById<TextView>(R.id.chatUserStatus)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        args = ChatFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(args.user.imageUrl!!)
            .placeholder(R.drawable.person)
            .dontAnimate()
            .into(circleImageView)
        textViewName.text = args.user.username
        textViewStatus.text = args.user.status

        chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

        binding.sendBtn.setOnClickListener {
            viewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.user.userid!!,
                args.user.username!!,
                args.user.imageUrl!!
            )
        }

        viewModel.getMessages(args.user.userid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }

    private fun initRecyclerView(list: List<Message>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)

        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = adapter
    }
}