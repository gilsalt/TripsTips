package com.tripstips.app.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.databinding.ItemPostDesignBinding
import com.tripstips.app.model.Post
import com.tripstips.app.view.activities.BaseActivity

class PostAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, post: Post)
        fun onItemLikeClick(position: Int,post: Post)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, mListener!!)
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    class PostViewHolder(
        private val binding: ItemPostDesignBinding,
        private val mListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                if (post.user?.userImage!!.isNotEmpty()){
                    Picasso.get().load(post.user.userImage)
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.placeholder)
                        .resize(100,100)
                        .centerCrop()
                        .into(userImage)
                }
                userName.text = post.user.name
                postDate.text = BaseActivity.getTimeAgo(post.timestamp)
                postLikeView.text = "${post.likes} Likes"
                postCommentView.text = "${post.totalComments} Comments"
                if (post.image.isNullOrEmpty()) {
                    postImage.visibility = View.GONE
                } else {
                    postImage.visibility = View.VISIBLE
                    Picasso.get().load(post.image)
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.placeholder)
                        .resize(600,400)
                        .centerCrop()
                        .into(postImage)
                }

                binding.postLikeView.setOnClickListener {
                    mListener.onItemLikeClick(layoutPosition,post)
                }

                binding.root.setOnClickListener {
                    mListener.onItemClick(layoutPosition, post)
                }

                postDescription.text = post.description
            }
        }
    }


}
