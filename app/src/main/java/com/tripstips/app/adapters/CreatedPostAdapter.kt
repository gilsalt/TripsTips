package com.tripstips.app.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.databinding.ItemCreatedPostDesignBinding
import com.tripstips.app.databinding.ItemPostDesignBinding
import com.tripstips.app.model.Post
import com.tripstips.app.view.activities.BaseActivity

class CreatedPostAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<CreatedPostAdapter.PostViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, post: Post)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemCreatedPostDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, mListener!!)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    class PostViewHolder(
        private val binding: ItemCreatedPostDesignBinding,
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
                postDescription.text = post.description

                binding.root.setOnClickListener {
                    mListener.onItemClick(layoutPosition, post)
                }
            }
        }
    }


}
