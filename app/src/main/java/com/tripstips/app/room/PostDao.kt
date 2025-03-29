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
}
