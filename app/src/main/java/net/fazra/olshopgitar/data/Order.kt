package net.fazra.olshopgitar.data

data class Order(
    val orderId: String = "",
    val items: Map<String, OrderItem> = emptyMap(),
    val totalPrice: Int = 0,
    val orderDate: String = ""
)