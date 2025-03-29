package com.tripstips.app.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tripstips.app.model.Comment

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsByPostId(postId: String): LiveData<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateComments(comments: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    fun deleteComment(commentId: String)
}