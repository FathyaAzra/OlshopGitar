package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.Order
import net.fazra.olshopgitar.data.User

class OrderViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val userRef = database.child("users")
    private val _orderHistory = MutableLiveData<List<Order>>()
    val orderHistory: LiveData<List<Order>> get() = _orderHistory

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
                    clearCartAfterOrder(userId)
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

    // Fetch user data from Firebase
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
}
