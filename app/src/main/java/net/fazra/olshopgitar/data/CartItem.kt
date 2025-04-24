package net.fazra.olshopgitar.data

data class CartItem(
    var itemId: String = "",
    var name: String = "",
    var price: Int = 0,
    var quantity: Int = 0,
    var photoUrl: String = "" // Add the photoUrl property here
)

