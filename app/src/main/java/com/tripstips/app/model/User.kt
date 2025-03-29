package com.tripstips.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val userImage: String = ""
):Parcelable{
    constructor():this("","","","")
}

