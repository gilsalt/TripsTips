package com.tripstips.app.viewmodelfactory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.viewmodel.PostViewModel

class PostViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
