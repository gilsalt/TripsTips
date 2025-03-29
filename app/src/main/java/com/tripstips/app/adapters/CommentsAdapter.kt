package com.tripstips.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tripstips.app.databinding.ItemCommentDesignBinding
import com.tripstips.app.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<Comment>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemDeleteClick(position: Int, comment: Comment)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = ItemCommentDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun submitList(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    class CommentViewHolder(private val binding: ItemCommentDesignBinding,private val mListener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.userName.text = comment.userName
            binding.commentTextView.text = comment.text
            binding.dateTimeTextView.text =
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                    Date(comment.timestamp)
                )
            binding.deleteButton.setOnClickListener {
                mListener.onItemDeleteClick(layoutPosition,comment)
            }
        }
    }
}