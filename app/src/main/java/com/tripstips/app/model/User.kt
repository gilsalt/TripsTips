package com.tripstips.app.model

import java.io.Serializable

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = ""
):Serializable{
    constructor():this("","","","")
}

