package net.fazra.olshopgitar.data

data class Cart(
    var cartId: String = "",
    var items: Map<String, CartItem> = emptyMap()
)