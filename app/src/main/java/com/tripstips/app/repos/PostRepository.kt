package com.tripstips.app.repos


import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Comment
import com.tripstips.app.model.Post
import com.tripstips.app.room.CommentDao
import com.tripstips.app.room.PostDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostRepository(private val postDao: PostDao,private val commentDao: CommentDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val commentsCollection = firestore.collection("comments")
    private val storageRef = FirebaseStorage.getInstance().reference.child("post_images")

    suspend fun syncPosts() {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = postsCollection.get().await()
                val firestorePosts = snapshot.toObjects(Post::class.java)
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

            if (firestoreId.isNotEmpty()) {
                val updatedPost = savedPost.copy(firestoreId = firestoreId)
                postDao.update(updatedPost)

                imageUri?.let {
                    uploadImageAndUpdatePost(updatedPost, it, callback)
                } ?: callback.onSuccess("Post added successfully!")
            }
        } catch (e: Exception) {
            callback.onFailure("Failed to add post: ${e.message}")
        }
    }


    suspend fun update(post: Post, newImageUri: Uri? = null, callback: PostCallback) {
        try {
            postDao.update(post)
           val temp = syncPostWithFirestore(post)

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

    private suspend fun syncPostWithFirestore(post: Post): String {
        try {
            val docRef = if (post.firestoreId.isNullOrEmpty()) {
                val ref = UUID.randomUUID().toString()
                 val updatePost = post.copy(firestoreId = ref)
                 postsCollection.document(ref).set(updatePost).await()
                ref
            } else {
                postsCollection.document(post.firestoreId).set(post).await()
                post.firestoreId
            }
            return docRef
        } catch (e: Exception) {
            return ""
        }
    }

    private suspend fun uploadImageAndUpdatePost(post: Post, newImageUri: Uri, callback: PostCallback) {
        val fileRef = storageRef.child("${post.firestoreId}.jpg")

        try {
            fileRef.putFile(newImageUri).await()
            val newDownloadUrl = fileRef.downloadUrl.await().toString()

            val updatedPost = post.copy(image = newDownloadUrl)
            postDao.update(updatedPost)
          val temp =  syncPostWithFirestore(updatedPost)

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

    fun getPostsByCity(city: String): LiveData<List<Post>> {
        return postDao.getPostsByCity(city)
    }

    fun getPostsByUserId(userId: String): LiveData<List<Post>> {
        return postDao.getPostsByUserId(userId)
    }

    fun getPostById(postId: String): LiveData<Post?> {
        return postDao.getPostByFireStoreId(postId)
    }

    suspend fun updateLikeCount(postId: String, userId: String) {
        withContext(Dispatchers.IO) {
            val post = postDao.getPostById(postId)

            post?.let {
                val likedByList = it.likedBy.toMutableList()

                val isLiked = likedByList.contains(userId)

                if (isLiked) {
                    likedByList.remove(userId)
                    it.likes -= 1
                } else {
                    likedByList.add(userId)
                    it.likes += 1
                }

                postDao.updateLikeData("${it.firestoreId}", it.likes, likedByList)

                val postRef = postsCollection.document("${it.firestoreId}")
                postRef.update(mapOf(
                    "likes" to it.likes,
                    "likedBy" to likedByList
                )).await()
            }
        }
    }

    fun getComments(postId: String): LiveData<List<Comment>> = commentDao.getCommentsByPostId(postId)

    suspend fun syncComments(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = commentsCollection.whereEqualTo("postId", postId).get().await()
                val firestoreComments = snapshot.toObjects(Comment::class.java)
                commentDao.insertOrUpdateComments(firestoreComments)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addComment(comment: Comment) {
        withContext(Dispatchers.IO) {
            try {
                commentDao.insertComment(comment)
                commentsCollection.document(comment.id).set(comment).await()
                updateCommentCount(comment.postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteComment(commentId: String,postId: String) {
        withContext(Dispatchers.IO) {
            try {
                commentsCollection.document(commentId).delete().await()
                commentDao.deleteComment(commentId)
                updateCommentCount(postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun updateCommentCount(postId: String) {
        withContext(Dispatchers.IO) {
            try {
                val snapshot = commentsCollection.whereEqualTo("postId", postId).get().await()
                val count = snapshot.size()
                postsCollection.document(postId).update("totalComments", count).await()
                postDao.updateCommentCount(postId, count)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
