package net.fazra.olshopgitar.data

data class Cart(
    val cartId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList()
)

