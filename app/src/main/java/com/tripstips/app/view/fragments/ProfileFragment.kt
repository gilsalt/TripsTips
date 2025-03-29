package com.tripstips.app.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.adapters.CreatedPostAdapter
import com.tripstips.app.adapters.PostAdapter
import com.tripstips.app.databinding.FragmentProfileBinding
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.view.activities.MainActivity
import com.tripstips.app.viewmodel.AuthViewModel
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory


class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao(),PostDatabase.getDatabase(requireContext()).commentDao()))
    }
    private lateinit var adapter:CreatedPostAdapter
    private var postsList = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val user = BaseActivity.loggedUser
        if (user != null){
            if (user.userImage.isNotEmpty()){
                Picasso.get().load(user.userImage)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.placeholder)
                    .resize(100,100)
                    .centerCrop()
                    .into(binding.profileImage)
            }
            binding.nameTv.text = user.name
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfileTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.signOutBtn.setOnClickListener {
            BaseActivity.loggedUser = null
            authViewModel.signOut()
            startActivity(Intent(requireActivity(),MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }).apply {
                requireActivity().finish()
            }
        }

        binding.createdPostsRecyclerview.layoutManager = GridLayoutManager(requireActivity(),2)
        binding.createdPostsRecyclerview.hasFixedSize()
        adapter = CreatedPostAdapter(postsList)
        binding.createdPostsRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : CreatedPostAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, post: Post) {
                val action = ProfileFragmentDirections.actionProfileFragmentToDetailFragment(post)
                findNavController().navigate(action)

            }
        })
        getPostsByUserId()
    }

    private fun getPostsByUserId() {
        postViewModel.getPostsByUserId("${BaseActivity.loggedUser?.userId}").observe(viewLifecycleOwner, Observer { posts ->
            if (posts.isNotEmpty()){
                postsList.clear()
            }
            postsList.addAll(posts)
            adapter.notifyItemChanged(0,postsList.size)
        })
    }
}