package com.tripstips.app.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "posts")
@Parcelize
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val city: String,
    val image: String? = null,
    val description: String,
    val address: String,
    val totalComments: Int = 0,
    val likes: Int = 0,
    val firestoreId: String? = null,
    val timestamp:Long = System.currentTimeMillis(),
   @Embedded val user: User? = null
):Parcelable

