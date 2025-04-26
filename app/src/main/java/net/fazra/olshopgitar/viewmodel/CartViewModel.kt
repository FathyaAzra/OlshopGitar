package net.fazra.olshopgitar.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.CartItem
import net.fazra.olshopgitar.data.User

class CartViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val userRef = database.child("users")

    private val _cartItems = MutableLiveData<Map<String, CartItem>>()
    val cartItems: LiveData<Map<String, CartItem>> get() = _cartItems

    fun loadCartFromFirebase(userId: String) {
        if (userId.isNotEmpty()) {
            userRef.child(userId).child("cart").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val cartId = snapshot.child("cartId").getValue(String::class.java) ?: userId
                    Log.d("CartViewModel", "Cart data fetched successfully: $cartId")

                    val itemsMap = snapshot.child("items").getValue(object : GenericTypeIndicator<Map<String, CartItem>>() {}) ?: emptyMap()
                    Log.d("CartViewModel", "Items fetched: $itemsMap")

                    val cart = Cart(cartId, itemsMap)
                    _cartItems.value = cart.items
                } else {
                    Log.d("CartViewModel", "Cart does not exist")
                    _cartItems.value = emptyMap()
                }
            }.addOnFailureListener {
                Log.e("CartViewModel", "Failed to load cart", it)
                _cartItems.value = emptyMap()
            }
        } else {
            _cartItems.value = emptyMap()
        }
    }

    fun addItemToCart(item: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            getUserData(userId) { user ->
                user?.let {
                    val updatedItems = it.cart.items.toMutableMap()
                    val existingItem = updatedItems[item.itemId]

                    if (existingItem != null) {
                        existingItem.quantity += item.quantity
                        updatedItems[item.itemId] = existingItem
                    } else {
                        updatedItems[item.itemId] = item
                    }

                    val updatedCart = it.cart.copy(items = updatedItems)
                    updateCart(userId, updatedCart)
                    _cartItems.value = updatedItems
                }
            }
        }
    }

    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            getUserData(userId) { user ->
                user?.let {
                    val updatedItems = it.cart.items.toMutableMap()
                    val existingItem = updatedItems[itemId]

                    if (existingItem != null) {
                        // Update the quantity of the existing item
                        existingItem.quantity = newQuantity
                        updatedItems[itemId] = existingItem

                        val updatedCart = it.cart.copy(items = updatedItems)
                        updateCart(userId, updatedCart)
                        _cartItems.value = updatedItems
                    }
                }
            }
        }
    }


    fun removeItemFromCart(itemId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            getUserData(userId) { user ->
                user?.let {
                    val updatedItems = it.cart.items.toMutableMap()
                    updatedItems.remove(itemId)

                    val updatedCart = it.cart.copy(items = updatedItems)
                    updateCart(userId, updatedCart)
                    _cartItems.value = updatedItems
                }
            }
        }
    }

    private fun updateCart(userId: String, newCart: Cart) {
        userRef.child(userId).child("cart").setValue(newCart).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _cartItems.value = newCart.items
            }
        }
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

