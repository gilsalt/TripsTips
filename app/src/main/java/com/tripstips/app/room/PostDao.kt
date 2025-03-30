package com.tripstips.app.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tripstips.app.model.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post): Long

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("SELECT * FROM posts WHERE firestoreId != 'NULL' ORDER BY id DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE city = :city AND firestoreId != 'NULL'")
    fun getPostsByCity(city: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId AND firestoreId != 'NULL'")
    fun getPostsByUserId(userId: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId AND firestoreId != 'NULL' LIMIT 1")
    fun getPostById(postId: String): Post?

    @Update
    fun updatePost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePosts(posts: List<Post>)

    @Query("UPDATE posts SET totalComments = :count WHERE firestoreId = :postId")
    fun updateCommentCount(postId: String, count: Int)
}
