package net.fazra.olshopgitar.data

data class User(
    val userId: String = "",
    val email: String = "",
    val cart: Cart = Cart(),
    val orderHistory: List<Order> = emptyList()
) {
    constructor() : this("", "", Cart(), emptyList())
}

