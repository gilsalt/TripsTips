package com.tripstips.app.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tripstips.app.model.User
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {

    companion object{
        var loggedUser:User?=null

        fun getTimeAgo(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - timestamp

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            return when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes minutes ago"
                hours < 24 -> "$hours hours ago"
                days < 7 -> "$days days ago"
                days < 30 -> "${days / 7} weeks ago"
                else -> "${days / 30} months ago"
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}