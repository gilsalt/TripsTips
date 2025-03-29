package com.tripstips.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",  // Firestore document ID
    val postId: String,    // Associated Post ID
    val userId: String,
    val userName: String,
    val text: String,
    val timestamp: Long
)
