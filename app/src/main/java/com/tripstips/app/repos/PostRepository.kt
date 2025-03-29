package com.tripstips.app.repos


import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Post
import com.tripstips.app.room.PostDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRepository(private val postDao: PostDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val storageRef = FirebaseStorage.getInstance().reference.child("post_images")

    suspend fun syncPosts() {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = postsCollection.get().await()
                val firestorePosts = snapshot.toObjects(Post::class.java)
                // ✅ Insert or update posts in Room
                postDao.insertOrUpdatePosts(firestorePosts)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun insert(post: Post, imageUri: Uri? = null, callback: PostCallback) {
        try {
            val postId = postDao.insert(post).toInt()
            val savedPost = post.copy(id = postId)

            val firestoreId = syncPostWithFirestore(savedPost)
            val updatedPost = savedPost.copy(firestoreId = firestoreId)
            postDao.update(updatedPost)

            imageUri?.let {
                uploadImageAndUpdatePost(updatedPost, it, callback)
            } ?: callback.onSuccess("Post added successfully!")

        } catch (e: Exception) {
            callback.onFailure("Failed to add post: ${e.message}")
        }
    }

    suspend fun update(post: Post, newImageUri: Uri? = null, callback: PostCallback) {
        try {
            postDao.update(post)
            syncPostWithFirestore(post)

            newImageUri?.let {
                uploadImageAndUpdatePost(post, it, callback)
            } ?: callback.onSuccess("Post updated successfully!")

        } catch (e: Exception) {
            callback.onFailure("Failed to update post: ${e.message}")
        }
    }

    suspend fun delete(post: Post, callback: PostCallback) {
        try {
            postDao.delete(post)
            post.firestoreId?.let { postsCollection.document(it).delete() }

            post.image?.let {
                if (it.isNotEmpty()) {
                    FirebaseStorage.getInstance().getReferenceFromUrl(it).delete().await()
                }
            }

            callback.onSuccess("Post deleted successfully!")
        } catch (e: Exception) {
            callback.onFailure("Failed to delete post: ${e.message}")
        }
    }

    private suspend fun syncPostWithFirestore(post: Post): String? {
        return try {
            val docRef = if (post.firestoreId.isNullOrEmpty()) {
                postsCollection.add(post).await()
            } else {
                postsCollection.document(post.firestoreId).set(post).await()
                postsCollection.document(post.firestoreId)
            }
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun uploadImageAndUpdatePost(post: Post, newImageUri: Uri, callback: PostCallback) {
        val fileRef = storageRef.child("${post.firestoreId}.jpg")

        try {
            fileRef.putFile(newImageUri).await()
            val newDownloadUrl = fileRef.downloadUrl.await().toString()

            val updatedPost = post.copy(image = newDownloadUrl)
            postDao.update(updatedPost)
            syncPostWithFirestore(updatedPost)

            callback.onSuccess("Image uploaded successfully!")

            post.image?.let {
                if (it.isNotEmpty()) {
                    FirebaseStorage.getInstance().getReferenceFromUrl(it).delete().await()
                }
            }
        } catch (e: Exception) {
            callback.onFailure("Failed to upload image: ${e.message}")
        }
    }

    // Fetch posts by city from Room
    fun getPostsByCity(city: String): LiveData<List<Post>> {
        return postDao.getPostsByCity(city)
    }

    // Fetch posts by userId from Room
    fun getPostsByUserId(userId: String): LiveData<List<Post>> {
        return postDao.getPostsByUserId(userId)
    }

    suspend fun updateLikeCount(postId: String, isLiked: Boolean) {
        withContext(Dispatchers.IO) {
            val post = postDao.getPostById(postId)
            if (post != null) {
                val newLikeCount = if (isLiked) post.likes + 1 else post.likes - 1
                post.likes = newLikeCount
                postDao.updatePost(post)
                postsCollection.document("${post.firestoreId}").update("likes", newLikeCount).await()
            }
        }
    }

}
