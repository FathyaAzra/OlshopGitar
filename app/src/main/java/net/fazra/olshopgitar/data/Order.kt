package net.fazra.olshopgitar.data

data class Order(
    val orderId: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val orderDate: String
)
