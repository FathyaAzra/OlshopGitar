package net.fazra.olshopgitar.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.CartItem
import net.fazra.olshopgitar.data.Item

class CartViewModel : ViewModel() {
    private val _cart = mutableStateOf(Cart())
    val cart get() = _cart

    val cartItems get() = _cart.value.items

    fun addItemToCart(item: Item, quantity: Int) {
        val newItem = CartItem(
            itemId = item.id,
            name = item.name,
            price = item.price.toDouble(),
            quantity = quantity
        )
        _cart.value = _cart.value.copy(items = _cart.value.items + newItem)
    }

    fun removeItemFromCart(itemId: Int) {
        _cart.value = _cart.value.copy(
            items = _cart.value.items.filterNot { it.itemId == itemId }
        )
    }

    fun updateItemQuantity(itemId: Int, quantity: Int) {
        _cart.value = _cart.value.copy(
            items = _cart.value.items.map {
                if (it.itemId == itemId) it.copy(quantity = quantity) else it
            }
        )
    }

    fun saveCartToFirebase(userId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId/cart")
        dbRef.setValue(_cart.value)
    }
}

