package com.tripstips.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentDetailBinding
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory


class DetailFragment : Fragment() {
    private lateinit var binding:FragmentDetailBinding
    private var post:Post?=null
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao(),PostDatabase.getDatabase(requireContext()).commentDao()))
    }
    var flag = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        val args: DetailFragmentArgs by navArgs()
        post = args.post
        return binding.root
    }

    private fun renderPostDetails(){
        post?.apply {
            binding.apply {
                if (user?.userImage!!.isNotEmpty()){
                    Picasso.get().load(user.userImage)
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.placeholder)
                        .resize(100,100)
                        .centerCrop()
                        .into(userImage)
                }
                userName.text = user.name
                postDate.text = BaseActivity.getTimeAgo(timestamp)
                postDescription.text = description
                val isLiked = post!!.likedBy.contains(BaseActivity.loggedUser?.userId)
                postLikeView.setCompoundDrawablesWithIntrinsicBounds(if (isLiked) R.drawable.ic_thumb_up_solid else R.drawable.ic_thumb_up_outline,0, 0, 0)
                postLikeView.text = "${likes} Likes"
                postCommentView.text = "${totalComments} Comments"
                if (image.isNullOrEmpty()) {
                    postImage.visibility = View.GONE
                } else {
                    postImage.visibility = View.VISIBLE
                    Picasso.get().load(image)
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.placeholder)
                        .resize(600,400)
                        .centerCrop()
                        .into(postImage)
                }
                if (user.userId == BaseActivity.loggedUser?.userId){
                    postEditView.visibility = View.VISIBLE
                    postDeleteView.visibility = View.VISIBLE
                }
                else{
                    postEditView.visibility = View.GONE
                    postDeleteView.visibility = View.GONE
                }

                postLikeView.setOnClickListener {
                    val userId = BaseActivity.loggedUser?.userId ?: return@setOnClickListener
                    val mutableLikedBy = post!!.likedBy.toMutableList()
                    var updatedLikes = post!!.likes

                    if (mutableLikedBy.contains(userId)) {
                        mutableLikedBy.remove(userId)
                        updatedLikes -= 1
                        postLikeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_outline, 0, 0, 0)
                    } else {
                        mutableLikedBy.add(userId)
                        updatedLikes += 1
                        postLikeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_solid, 0, 0, 0)
                    }

                    post = post!!.copy(likedBy = mutableLikedBy, likes = updatedLikes)
                    postLikeView.text = "$updatedLikes Likes"

                    postViewModel.updateLikeCount("${post!!.firestoreId}", userId)
                }



                postCommentView.setOnClickListener {
                    val action = DetailFragmentDirections.actionDetailFragmentToPostCommentSheetFragment(post!!)
                    findNavController().navigate(action)

                }

                postEditView.setOnClickListener {
                    val action = DetailFragmentDirections.actionDetailFragmentToEditPostFragment(post!!)
                    findNavController().navigate(action)
                }

                postDeleteView.setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(requireActivity())
                    builder.setTitle("Delete Warning")
                    builder.setMessage("Are you sure you want to delete the post?")
                    builder.setNegativeButton("Cancel"){dialog,which->
                        dialog.dismiss()
                    }
                    builder.setPositiveButton("Delete"){dialog,which->
                        dialog.dismiss()
                        postViewModel.delete(post!!,object :PostCallback{
                            override fun onSuccess(message: String) {
                                Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }

                            override fun onFailure(error: String) {
                                Toast.makeText(requireActivity(),error,Toast.LENGTH_SHORT).show()
                            }
                        })
                    }

                    val alert = builder.create()
                    alert.show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        renderPostDetails()
        postViewModel.getPostById("${post!!.firestoreId}").observe(viewLifecycleOwner) { updatedPost ->
            if (updatedPost != null) {
                post = updatedPost
                renderPostDetails()
            }
        }
    }
}