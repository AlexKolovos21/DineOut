package com.example.dineout.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class MenuCategory(
    val id: String,
    val name: String,
    val description: String,
    val items: List<MenuItem>
) : Parcelable 