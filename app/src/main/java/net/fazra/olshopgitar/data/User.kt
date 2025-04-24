package net.fazra.olshopgitar.data

data class User(
    val userId: String = "",
    val email: String = "",
    val cart: Cart = Cart(),
    val orderHistory: Map<String, Order> = emptyMap()
)