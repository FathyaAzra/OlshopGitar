package net.fazra.olshopgitar.data

data class Order(
    val orderId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Int = 0,
    val orderDate: String = ""
)
