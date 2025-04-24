package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseUser
import net.fazra.olshopgitar.data.User
import net.fazra.olshopgitar.data.Cart
import net.fazra.olshopgitar.data.Order

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser == null){
            _authState.value = AuthState.Unauthenticated
        } else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if user data exists in Firebase
                        checkIfUserExists(user)
                    }
                } else{
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    if (user != null) {
                        // Save user data after sign-up
                        saveUserData(user)
                    }
                } else{
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    private fun checkIfUserExists(user: FirebaseUser) {
        val userRef = database.child("users").child(user.uid)
        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // Save user data if it doesn't exist
                saveUserData(user)
            } else {
                _authState.value = AuthState.Authenticated
            }
        }
    }

    private fun saveUserData(user: FirebaseUser) {
        val userId = user.uid
        val email = user.email ?: "Unknown"

        // Create User object
        val newUser = User(
            userId = userId,
            email = email,
            cart = Cart(
                cartId = TODO(),
                userId = TODO(),
                items = TODO()
            ), // Empty cart by default
            orderHistory = listOf() // Empty order history by default
        )

        val userRef = database.child("users").child(userId)

        // Save the user data to Firebase Realtime Database
        userRef.setValue(newUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Failed to save user data")
            }
        }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun getUserData(userId: String, onResult: (User?) -> Unit) {
        val userRef = database.child("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }

    // Update user's cart
    fun updateCart(userId: String, newCart: Cart) {
        val userRef = database.child("users").child(userId)
        userRef.child("cart").setValue(newCart)
    }

    // Add completed order to order history
    fun addOrderToHistory(userId: String, order: Order) {
        val userRef = database.child("users").child(userId).child("orderHistory")
        userRef.push().setValue(order)
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}