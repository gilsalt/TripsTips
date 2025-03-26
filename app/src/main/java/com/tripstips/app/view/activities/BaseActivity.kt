package com.tripstips.app.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tripstips.app.model.User

open class BaseActivity : AppCompatActivity() {

    companion object{
        var loggedUser:User?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}