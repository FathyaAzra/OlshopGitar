package net.fazra.olshopgitar.data

data class Item(
    val name: String,
    val category: String,
    val price : Int,
    val description: String,
    val itemleft : Int,
    val photoResId: Int,
)
