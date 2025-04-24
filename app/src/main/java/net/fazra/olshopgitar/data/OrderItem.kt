package net.fazra.olshopgitar.data

data class OrderItem(
    val itemId: String = "",
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 0
)