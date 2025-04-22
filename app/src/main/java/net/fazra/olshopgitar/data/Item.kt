package net.fazra.olshopgitar.data

data class Item(
    val id: Int,
    val name: String,
    val category: String,
    val price : Int,
    val description: String,
    val stock : Int,
    val photoResId: Int,
)
