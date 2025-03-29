package com.tripstips.app.view.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tripstips.app.R
import com.tripstips.app.adapters.CommentsAdapter
import com.tripstips.app.databinding.FragmentPostCommentSheetBinding
import com.tripstips.app.model.Comment
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory
import java.util.UUID


class PostCommentSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentPostCommentSheetBinding
    private var post: Post?=null
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao(),PostDatabase.getDatabase(requireContext()).commentDao()))
    }
    private lateinit var adapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostCommentSheetBinding.inflate(inflater, container, false)

        val args:PostCommentSheetFragmentArgs by navArgs()
        post = args.post
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel.syncComments("${post?.id}")

        binding.commentsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.commentsRecyclerview.hasFixedSize()
        adapter = CommentsAdapter()
        binding.commentsRecyclerview.adapter = adapter
        postViewModel.getComments("${post?.firestoreId}").observe(viewLifecycleOwner) { comments ->
           adapter.submitList(comments)
        }

        adapter.setOnItemClickListener(object :CommentsAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {

            }

            override fun onItemDeleteClick(position: Int, comment: Comment) {
                postViewModel.deleteComment(comment.id,"${post?.firestoreId}")
            }
        })

        binding.btnSend.setOnClickListener {
            val commentText = binding.commentInputBox.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                binding.commentInputBox.text?.clear()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let { sheet ->

                sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                sheet.requestLayout()

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // Ensure it's expanded
            }
        }

        return dialog
    }

    private fun addComment(commentText: String) {
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            postId = "${post?.firestoreId}",
            userId = "${BaseActivity.loggedUser?.userId}",
            userName = "${BaseActivity.loggedUser?.name}",
            text = commentText,
            timestamp = System.currentTimeMillis()
        )
        postViewModel.addComment(newComment)
    }
}