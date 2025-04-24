package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import net.fazra.olshopgitar.data.User
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.CartItem
import net.fazra.olshopgitar.data.Order

class CartViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userRef = database.child("users")

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    // Function to load the cart directly (triggered by user authentication in the activity or fragment)
    fun loadCartFromFirebase(userId: String) {
        userRef.child(userId).child("cart").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val cart = snapshot.getValue(Cart::class.java)
                _cartItems.value = cart?.items ?: emptyList()
            } else {
                _cartItems.value = emptyList() // No cart found
            }
        }
    }

    fun addItemToCart(userId: String, item: CartItem) {
        getUserData(userId) { user ->
            user?.let {
                // Check if the item already exists in the cart
                val updatedItems = it.cart.items.toMutableList()
                val existingItemIndex = updatedItems.indexOfFirst { cartItem -> cartItem.itemId == item.itemId }

                if (existingItemIndex != -1) {
                    // If the item exists, increase the quantity
                    val existingItem = updatedItems[existingItemIndex]
                    existingItem.quantity += item.quantity
                    updatedItems[existingItemIndex] = existingItem
                } else {
                    // Otherwise, add the new item
                    updatedItems.add(item)
                }

                val updatedCart = it.cart.copy(items = updatedItems)
                updateCart(userId, updatedCart)
                _cartItems.value = updatedItems // Update LiveData
            }
        }
    }

    fun removeItemFromCart(userId: String, itemId: String) {
        getUserData(userId) { user ->
            user?.let {
                // Remove the item by its itemId
                val updatedItems = it.cart.items.filterNot { cartItem -> cartItem.itemId == itemId }
                val updatedCart = it.cart.copy(items = updatedItems)
                updateCart(userId, updatedCart)
                _cartItems.value = updatedItems // Update LiveData
            }
        }
    }

    fun updateCart(userId: String, newCart: Cart) {
        userRef.child(userId).child("cart").setValue(newCart)
    }

    fun checkout(userId: String, order: Order) {
        getUserData(userId) { user ->
            user?.let {
                // Add the order to the user's order history
                addOrderToHistory(userId, order)

                // Clear the cart after checkout
                val clearedCart = Cart(cartId = it.cart.cartId, userId = userId, items = emptyList())
                updateCart(userId, clearedCart)
                _cartItems.value = emptyList() // Empty the cart in LiveData after checkout
            }
        }
    }

    private fun addOrderToHistory(userId: String, order: Order) {
        userRef.child(userId).child("orderHistory").push().setValue(order)
    }

    private fun getUserData(userId: String, onResult: (User?) -> Unit) {
        userRef.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }
}
