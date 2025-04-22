package com.example.dineout.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Order(
    val id: String,
    val restaurantId: String,
    val restaurantName: String = "",
    val items: Map<MenuItem, Int>,
    val total: Double,
    val date: Long = System.currentTimeMillis(),
    val status: String,
    val deliveryAddress: String,
    val paymentMethod: String
) : Parcelable {
    
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(date))
    }
    
    fun getTotalItems(): Int {
        return items.values.sum()
    }
    
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_CONFIRMED = "Confirmed"
        const val STATUS_PREPARING = "Preparing"
        const val STATUS_READY = "Ready for Pickup"
        const val STATUS_DELIVERING = "Out for Delivery"
        const val STATUS_DELIVERED = "Delivered"
        const val STATUS_CANCELLED = "Cancelled"
    }
}

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val specialInstructions: String = ""
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
} 