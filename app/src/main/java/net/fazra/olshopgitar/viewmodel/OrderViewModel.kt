package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.Order
import net.fazra.olshopgitar.data.User
import net.fazra.olshopgitar.data.Item
import java.lang.Exception

class OrderViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val userRef = database.child("users")
    private val itemRef = database.child("items")
    private val _orderHistory = MutableLiveData<List<Order>>()
    val orderHistory: LiveData<List<Order>> get() = _orderHistory
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun loadOrderHistoryFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            userRef.child(userId).child("orderHistory").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val orders = snapshot.children.mapNotNull { orderSnapshot ->
                        orderSnapshot.getValue(Order::class.java)
                    }
                    _orderHistory.value = orders
                } else {
                    _orderHistory.value = emptyList()
                }
            }
        } else {
            _orderHistory.value = emptyList()
        }
    }

    fun placeOrder(order: Order) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            getUserData(userId) { user ->
                user?.let {
                    addOrderToHistory(userId, order)
                    coroutineScope.launch { // Use coroutineScope
                        try {
                            updateItemStocks(order.items)
                            clearCartAfterOrder(userId)
                        } catch (e: Exception) {
                            println("Error placing order or updating stock: ${e.message}")
                            clearCartAfterOrder(userId)
                        }
                    }
                }
            }
        }
    }

    private fun addOrderToHistory(userId: String, order: Order) {
        userRef.child(userId).child("orderHistory").push().setValue(order).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadOrderHistoryFromFirebase()
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

    private fun clearCartAfterOrder(userId: String) {
        val clearedCart = Cart(cartId = "", items = emptyMap())
        userRef.child(userId).child("cart").setValue(clearedCart).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Cart cleared successfully!")
            } else {
                println("Failed to clear the cart.")
            }
        }
    }

    private suspend fun updateItemStocks(orderItems: Map<String, net.fazra.olshopgitar.data.OrderItem>) {
        for ((itemId, orderItem) in orderItems) {
            try {
                val itemSnapshot = itemRef.child(itemId).get().await()
                if (itemSnapshot.exists()) {
                    val item = itemSnapshot.getValue(Item::class.java)
                    if (item != null) {
                        val newStock = item.stock - orderItem.quantity
                        if (newStock >= 0) {
                            itemRef.child(itemId).child("stock").setValue(newStock).await()
                        } else {
                            throw Exception("Insufficient stock for item: ${orderItem.name}")
                        }
                    } else {
                        throw Exception("Item is null")
                    }
                } else {
                    throw Exception("Item not found: $itemId")
                }
            } catch (e: Exception) {
                println("Error updating stock for item $itemId: ${e.message}")
                throw e
            }
        }
    }
}
