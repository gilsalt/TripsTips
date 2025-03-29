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

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE city = :city")
    fun getPostsByCity(city: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getPostsByUserId(userId: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    fun getPostById(postId: String): Post?

    @Update
    fun updatePost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdatePosts(posts: List<Post>)
}
