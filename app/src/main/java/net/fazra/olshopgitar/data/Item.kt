package net.fazra.olshopgitar.data

data class Item(
    val id: Int = 0,
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val description: String = "",
    val stock: Int = 0,
    val photoUrl: String = ""
)