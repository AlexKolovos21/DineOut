package com.example.dineout.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val allergens: List<String> = emptyList(),
    val calories: Int? = null,
    val preparationTime: Int? = null // in minutes
) : Parcelable 