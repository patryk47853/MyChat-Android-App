package pl.mychat.patrykkotlin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.patrkkoltin.fragments.ChatfromHomeArgs
import de.hdodenhof.circleimageview.CircleImageView
import pl.mychat.patrykkotlin.Utils
import pl.mychat.patrykkotlin.adapter.MessageAdapter
import pl.mychat.patrykkotlin.modal.Message
import pl.mychat.patrykkotlin.mvvm.ChatAppViewModel
import pl.patrykkotlin.mychat.R
import pl.patrykkotlin.mychat.databinding.FragmentChatfromHomeBinding
import pl.patrykkotlin.mychat.databinding.FragmentHomeChatBinding

class HomeChatAdapter : Fragment() {

    private lateinit var args: ChatfromHomeArgs
    private lateinit var binding: FragmentHomeChatBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)

        args = ChatfromHomeArgs.fromBundle(requireArguments())

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(args.recentchats.friendsimage!!)
            .placeholder(R.drawable.person)
            .dontAnimate()
            .into(circleImageView)

        textViewName.text = args.recentchats.name

        binding.chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatfromHome_to_homeFragment)
        }

        binding.sendBtn.setOnClickListener {
            viewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!
            )
        }

        viewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
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