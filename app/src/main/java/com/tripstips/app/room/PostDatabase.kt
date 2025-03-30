package com.tripstips.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tripstips.app.model.Comment
import com.tripstips.app.model.Post
import com.tripstips.app.utils.Converters

@Database(entities = [Post::class,Comment::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun commentDao():CommentDao

    companion object {
        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getDatabase(context: Context): PostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    "trips_tips_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
