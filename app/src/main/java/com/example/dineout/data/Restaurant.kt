package com.example.dineout.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

@Parcelize
data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val imageUrl: String? = null,
    val location: LatLng,
    val address: String,
    val phone: String,
    val description: String,
    val priceRange: String,
    val openingHours: Map<String, String>,
    val features: List<String> = emptyList(),
    val menu: List<MenuCategory> = emptyList(),
    val isOpen: Boolean = true,
    val isFavorite: Boolean = false,
    val distance: Double? = null, // in kilometers
    val website: String? = null,
    val email: String? = null,
    val socialMedia: Map<String, String> = emptyMap(),
    val paymentMethods: List<String> = emptyList(),
    val reservations: Boolean = false,
    val delivery: Boolean = false,
    val takeout: Boolean = false,
    val outdoorSeating: Boolean = false,
    val parking: Boolean = false,
    val wifi: Boolean = false,
    val accessibility: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) : Parcelable {
    fun isOpenNow(): Boolean {
        if (!isOpen) return false
        
        val now = java.util.Calendar.getInstance()
        val currentDay = now.get(java.util.Calendar.DAY_OF_WEEK)
        val currentTime = now.get(java.util.Calendar.HOUR_OF_DAY) * 100 + 
                         now.get(java.util.Calendar.MINUTE)
        
        val dayNames = listOf("Sunday", "Monday", "Tuesday", "Wednesday", 
                            "Thursday", "Friday", "Saturday")
        val currentDayName = dayNames[currentDay - 1]
        
        val hours = openingHours[currentDayName] ?: return false
        val (openTime, closeTime) = hours.split("-").map { it.trim() }
        
        val openHour = openTime.split(":").map { it.toInt() }
        val closeHour = closeTime.split(":").map { it.toInt() }
        
        val openTimeMinutes = openHour[0] * 100 + openHour[1]
        val closeTimeMinutes = closeHour[0] * 100 + closeHour[1]
        
        return currentTime in openTimeMinutes..closeTimeMinutes
    }
    
    fun getAveragePrice(): Double {
        return menu.flatMap { it.items }
            .map { it.price }
            .average()
    }
    
    fun getVegetarianOptions(): List<MenuItem> {
        return menu.flatMap { it.items }
            .filter { it.isVegetarian }
    }
    
    fun getMenuByCategory(categoryId: String): MenuCategory? {
        return menu.find { it.id == categoryId }
    }
}

// Sample restaurants
val sampleRestaurants = listOf(
    Restaurant(
        id = "1",
        name = "Taverna Platanos",
        cuisine = "Greek",
        rating = 4.7,
        address = "15 Dionysiou Areopagitou, Athens 11742",
        phone = "+30 21 0923 8260",
        description = "Traditional Greek taverna serving authentic dishes in a cozy atmosphere with outdoor seating under a huge plane tree.",
        priceRange = "€€",
        location = LatLng(37.9685, 23.7319),
        imageUrl = "https://images.unsplash.com/photo-1535922829847-25ee77a335dc?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z3JlZWslMjByZXN0YXVyYW50fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        openingHours = mapOf(
            "Monday" to "12:00 - 23:00",
            "Tuesday" to "12:00 - 23:00", 
            "Wednesday" to "12:00 - 23:00",
            "Thursday" to "12:00 - 23:00",
            "Friday" to "12:00 - 00:00",
            "Saturday" to "12:00 - 00:00",
            "Sunday" to "12:00 - 23:00"
        ),
        features = listOf("Outdoor Seating", "Traditional", "Family Friendly"),
        menu = listOf(
            MenuCategory(
                id = "appetizers1",
                name = "Appetizers",
                description = "Traditional Greek starters to share",
                items = listOf(
                    MenuItem(
                        id = "1",
                        name = "Tzatziki",
                        description = "Creamy yogurt dip with cucumber, garlic and olive oil",
                        price = 4.50,
                        imageUrl = "https://images.unsplash.com/photo-1633436375153-d7045cb93e37?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8dHphdHppa2l8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "2",
                        name = "Dolmades",
                        description = "Grape leaves stuffed with rice and herbs",
                        price = 5.80,
                        imageUrl = "https://images.unsplash.com/photo-1556388158-158ea5ccacbd?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8ZG9sbWFkZXN8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "3",
                        name = "Saganaki",
                        description = "Pan-fried cheese with lemon",
                        price = 6.50,
                        imageUrl = "https://images.unsplash.com/photo-1558985045-db258e612831?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8c2FnYW5ha2l8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "4",
                        name = "Greek Salad",
                        description = "Tomatoes, cucumbers, onions, feta cheese and olives",
                        price = 7.50,
                        imageUrl = "https://images.unsplash.com/photo-1551528701-cb99ef6edc31?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z3JlZWslMjBzYWxhZHxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    )
                )
            ),
            MenuCategory(
                id = "mains1",
                name = "Main Dishes",
                description = "Classic Greek mains cooked with traditional recipes",
                items = listOf(
                    MenuItem(
                        id = "5",
                        name = "Moussaka",
                        description = "Layers of eggplant, potatoes and seasoned ground beef topped with béchamel sauce",
                        price = 12.80,
                        imageUrl = "https://images.unsplash.com/photo-1574484284002-952d92456975?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bW91c3Nha2F8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "6",
                        name = "Souvlaki Platter",
                        description = "Grilled skewers of marinated pork served with pita, tzatziki and fries",
                        price = 14.50,
                        imageUrl = "https://images.unsplash.com/photo-1561626423-3509f6e86c13?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c291dmxha2l8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "7",
                        name = "Pastitsio",
                        description = "Baked pasta with ground beef and béchamel sauce",
                        price = 12.00,
                        imageUrl = "https://images.unsplash.com/photo-1623259838743-9f1e884fba59?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cGFzdGl0c2lvfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "8",
                        name = "Gemista",
                        description = "Tomatoes and peppers stuffed with rice and herbs",
                        price = 11.50,
                        imageUrl = "https://images.unsplash.com/photo-1626196340106-d94fb916c889?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z2VtaXN0YXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    )
                )
            ),
            MenuCategory(
                id = "desserts1",
                name = "Desserts",
                description = "Sweet treats to end your meal",
                items = listOf(
                    MenuItem(
                        id = "9",
                        name = "Baklava",
                        description = "Phyllo pastry with nuts and honey",
                        price = 5.50,
                        imageUrl = "https://images.unsplash.com/photo-1519676867240-f03562e64548?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8YmFrbGF2YXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "10",
                        name = "Galaktoboureko",
                        description = "Custard-filled phyllo pastry with syrup",
                        price = 6.00,
                        imageUrl = "https://images.unsplash.com/photo-1551406368-1697d1552e4f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z2FsYWt0b2JvdXJla298ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "11",
                        name = "Greek Yogurt with Honey",
                        description = "Creamy yogurt topped with honey and walnuts",
                        price = 4.50,
                        imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z3JlZWslMjB5b2d1cnR8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    )
                )
            )
        ),
        isOpen = true,
        isFavorite = false,
        distance = 1.2
    ),
    Restaurant(
        id = "2",
        name = "Psaras Taverna",
        cuisine = "Seafood",
        rating = 4.5,
        address = "22 Aiolou, Athens 10551",
        phone = "+30 21 0321 8733",
        description = "Seafood restaurant specializing in fresh catches of the day with a beautiful view of the harbor.",
        priceRange = "€€",
        location = LatLng(37.9749, 23.7283),
        imageUrl = "https://images.unsplash.com/photo-1576675466969-38eeae4b41f6?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8Z3JlZWslMjByZXN0YXVyYW50fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        openingHours = mapOf(
            "Monday" to "18:00 - 23:00",
            "Tuesday" to "18:00 - 23:00", 
            "Wednesday" to "18:00 - 23:00",
            "Thursday" to "18:00 - 23:00",
            "Friday" to "18:00 - 00:00",
            "Saturday" to "12:00 - 00:00",
            "Sunday" to "12:00 - 23:00"
        ),
        features = listOf("Seafood", "Outdoor Seating", "Sea View"),
        menu = listOf(
            MenuCategory(
                id = "appetizers2",
                name = "Appetizers",
                description = "Fresh seafood starters",
                items = listOf(
                    MenuItem(
                        id = "12",
                        name = "Grilled Octopus",
                        description = "Tender octopus grilled with olive oil and lemon",
                        price = 12.00,
                        imageUrl = "https://images.unsplash.com/photo-1614138159368-747d340c9aab?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z3JpbGxlZCUyMG9jdG9wdXN8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "13",
                        name = "Fried Calamari",
                        description = "Crispy fried calamari served with garlic sauce",
                        price = 8.50,
                        imageUrl = "https://images.unsplash.com/photo-1599487325555-418e502fecc4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8ZnJpZWQlMjBjYWxhbWFyaXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "14",
                        name = "Taramasalata",
                        description = "Fish roe dip with olive oil and lemon",
                        price = 5.00,
                        imageUrl = "https://images.unsplash.com/photo-1589726030399-8a10a94e2cab?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8dGFyYW1hc2FsYXRhfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    )
                )
            ),
            MenuCategory(
                id = "mains2",
                name = "Main Dishes",
                description = "Fresh seafood from the Aegean Sea",
                items = listOf(
                    MenuItem(
                        id = "15",
                        name = "Grilled Sea Bream",
                        description = "Fresh sea bream grilled with olive oil, lemon and herbs",
                        price = 18.00,
                        imageUrl = "https://images.unsplash.com/photo-1559847844-5315695aa267?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Z3JpbGxlZCUyMGZpc2h8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "16",
                        name = "Seafood Pasta",
                        description = "Pasta with mixed seafood in tomato sauce",
                        price = 16.50,
                        imageUrl = "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c2VhZm9vZCUyMHBhc3RhfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "17",
                        name = "Shrimp Saganaki",
                        description = "Shrimp cooked in tomato sauce with feta cheese",
                        price = 15.00,
                        imageUrl = "https://images.unsplash.com/photo-1627308595171-d1b5d67129c4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c2hyaW1wJTIwc2FnYW5ha2l8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    )
                )
            )
        ),
        isOpen = true,
        isFavorite = false,
        distance = 0.8
    ),
    Restaurant(
        id = "3",
        name = "To Kafeneio",
        cuisine = "Greek",
        rating = 4.6,
        address = "Loukianou 26, Athens 10675",
        phone = "+30 21 0722 0920",
        description = "A traditional Greek café serving home-style food in a rustic setting with a lovely courtyard.",
        priceRange = "€",
        location = LatLng(37.9783, 23.7414),
        imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8Z3JlZWslMjByZXN0YXVyYW50fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
        openingHours = mapOf(
            "Monday" to "07:00 - 23:00",
            "Tuesday" to "07:00 - 23:00", 
            "Wednesday" to "07:00 - 23:00",
            "Thursday" to "07:00 - 23:00",
            "Friday" to "07:00 - 00:00",
            "Saturday" to "08:00 - 00:00",
            "Sunday" to "08:00 - 22:00"
        ),
        features = listOf("Breakfast", "Coffee", "Traditional", "Outdoor Seating"),
        menu = listOf(
            MenuCategory(
                id = "breakfast",
                name = "Breakfast",
                description = "Traditional Greek breakfast options",
                items = listOf(
                    MenuItem(
                        id = "18",
                        name = "Greek Yogurt with Honey and Nuts",
                        description = "Thick Greek yogurt with honey and mixed nuts",
                        price = 6.00,
                        imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z3JlZWslMjB5b2d1cnR8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "19",
                        name = "Spanakopita",
                        description = "Spinach and feta cheese pie in phyllo pastry",
                        price = 4.50,
                        imageUrl = "https://images.unsplash.com/photo-1574313996699-bb7aca4f4f1c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8c3BhbmFrb3BpdGF8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "20",
                        name = "Greek Omelet",
                        description = "Eggs with tomatoes, feta cheese and herbs",
                        price = 7.50,
                        imageUrl = "https://images.unsplash.com/photo-1590797144027-eded5a63e192?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8b21lbGV0fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    )
                )
            ),
            MenuCategory(
                id = "mains3",
                name = "Main Dishes",
                description = "Home-style Greek favorites",
                items = listOf(
                    MenuItem(
                        id = "21",
                        name = "Beef Stifado",
                        description = "Slow-cooked beef stew with onions and spices",
                        price = 13.50,
                        imageUrl = "https://images.unsplash.com/photo-1504669887875-b8ba0ba7e294?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YmVlZiUyMHN0ZXd8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    ),
                    MenuItem(
                        id = "22",
                        name = "Imam Baildi",
                        description = "Stuffed eggplant with tomato sauce and herbs",
                        price = 10.00,
                        imageUrl = "https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8c3R1ZmZlZCUyMGVnZ3BsYW50fGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "23",
                        name = "Chicken Souvlaki Plate",
                        description = "Grilled chicken skewers with pita, salad and tzatziki",
                        price = 11.50,
                        imageUrl = "https://images.unsplash.com/photo-1561626423-3509f6e86c13?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8c291dmxha2l8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = false
                    )
                )
            ),
            MenuCategory(
                id = "drinks",
                name = "Drinks",
                description = "Beverages to accompany your meal",
                items = listOf(
                    MenuItem(
                        id = "24",
                        name = "Greek Coffee",
                        description = "Traditional Greek coffee served in a small cup",
                        price = 2.50,
                        imageUrl = "https://images.unsplash.com/photo-1550681560-af9bc1cb339e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Z3JlZWslMjBjb2ZmZWV8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "25",
                        name = "House Wine (500ml)",
                        description = "Local house wine, red or white",
                        price = 8.00,
                        imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8d2luZXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    ),
                    MenuItem(
                        id = "26",
                        name = "Ouzo",
                        description = "Traditional Greek anise-flavored spirit",
                        price = 3.50,
                        imageUrl = "https://images.unsplash.com/photo-1607622750671-6cd9a99eabd1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8b3V6b3xlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                        isVegetarian = true
                    )
                )
            )
        ),
        isOpen = true,
        isFavorite = false,
        distance = 1.5
    )
) 