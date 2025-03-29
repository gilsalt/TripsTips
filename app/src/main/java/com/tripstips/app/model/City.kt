package com.tripstips.app.model

import android.os.Parcelable
import com.tripstips.app.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(val name: String, val imageRes: Int):Parcelable

val cities = listOf(
    City("Berlin", R.drawable.berlin),
    City("Roma", R.drawable.rome),
    City("London", R.drawable.london),
    City("Prague", R.drawable.prague),
    City("Budapest", R.drawable.budapest),
    City("Paris", R.drawable.paris),
    City("Barcelona", R.drawable.barcelona)
)

