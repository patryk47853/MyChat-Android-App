@file:Suppress("DEPRECATION")

package pl.mychat.patrykkotlin.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.patrkkoltin.fragments.HomeFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import pl.mychat.patrykkotlin.activity.SignInActivity
import pl.mychat.patrykkotlin.adapter.OnChatClicked
import pl.mychat.patrykkotlin.adapter.OnItemClickListener
import pl.mychat.patrykkotlin.adapter.RecentChatAdapter
import pl.mychat.patrykkotlin.adapter.UserAdapter
import pl.mychat.patrykkotlin.modal.RecentChat
import pl.mychat.patrykkotlin.modal.User
import pl.mychat.patrykkotlin.mvvm.ChatAppViewModel
import pl.patrykkotlin.mychat.R
import pl.patrykkotlin.mychat.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), OnItemClickListener, OnChatClicked {

    lateinit var rvUsers: RecyclerView
    lateinit var rvRecentChats: RecyclerView
    lateinit var adapter: UserAdapter
    lateinit var viewModel: ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentadapter: RecentChatAdapter
    lateinit var firestore: FirebaseFirestore
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        toolbar = view.findViewById(R.id.toolbarMain)
        val logoutimage = toolbar.findViewById<ImageView>(R.id.logOut)
        circleImageView = toolbar.findViewById(R.id.tlImage)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(circleImageView)
        })

        firestore = FirebaseFirestore.getInstance()

        val firebaseAuth = FirebaseAuth.getInstance()

        logoutimage.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }

        rvUsers = view.findViewById(R.id.rvUsers)
        rvRecentChats = view.findViewById(R.id.rvRecentChats)
        adapter = UserAdapter()
        recentadapter = RecentChatAdapter()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(activity)

        rvUsers.layoutManager = layoutManager
        rvRecentChats.layoutManager = layoutManager2

        viewModel.getUsers().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            rvUsers.adapter = adapter
        })

        circleImageView.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }

        adapter.setOnClickListener(this)

        viewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
            recentadapter.setList(it)
            rvRecentChats.adapter = recentadapter
        })

        recentadapter.setOnChatClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onUserSelected(position: Int, user: User) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(user)
        view?.findNavController()?.navigate(action)
    }

    override fun onChatClickedItem(position: Int, chatList: RecentChat) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatfromHome(chatList)
        view?.findNavController()?.navigate(action)
    }
}
