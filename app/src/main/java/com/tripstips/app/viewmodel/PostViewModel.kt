package com.tripstips.app.viewmodel


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    fun syncPosts() {
        viewModelScope.launch {
            repository.syncPosts()
        }
    }

    fun insert(post: Post, imageUri: Uri? = null, callback: PostCallback) {
        viewModelScope.launch {
            repository.insert(post, imageUri, callback)
        }
    }

    fun update(post: Post, newImageUri: Uri? = null, callback: PostCallback) {
        viewModelScope.launch {
            repository.update(post, newImageUri, callback)
        }
    }

    fun delete(post: Post, callback: PostCallback) {
        viewModelScope.launch {
            repository.delete(post, callback)
        }
    }

    // Fetch posts by city from Room
    fun getPostsByCity(city: String): LiveData<List<Post>> {
        return repository.getPostsByCity(city)
    }

    // Fetch posts by userId from Room
    fun getPostsByUserId(userId: String): LiveData<List<Post>> {
        return repository.getPostsByUserId(userId)
    }

    fun updateLikeCount(postId: String, isLiked: Boolean) {
        viewModelScope.launch {
            repository.updateLikeCount(postId, isLiked)
        }
    }
}
