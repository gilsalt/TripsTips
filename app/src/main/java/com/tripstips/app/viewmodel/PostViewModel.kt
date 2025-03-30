package com.tripstips.app.viewmodel


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Comment
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

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

    fun getPostsByCity(city: String): LiveData<List<Post>> {
        return repository.getPostsByCity(city)
    }

    fun getPostsByUserId(userId: String): LiveData<List<Post>> {
        return repository.getPostsByUserId(userId)
    }

    fun getPostById(postId: String): LiveData<Post?> {
        return repository.getPostById(postId)
    }

    fun updateLikeCount(postId: String, userId: String) {
        viewModelScope.launch {
            repository.updateLikeCount(postId, userId)
        }
    }

    fun getComments(postId: String): LiveData<List<Comment>> = repository.getComments(postId)

    fun syncComments(postId: String) {
        viewModelScope.launch {
            repository.syncComments(postId)
        }
    }

    fun addComment(comment: Comment) {
        viewModelScope.launch {
            repository.addComment(comment)
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId,postId)
        }
    }
}
