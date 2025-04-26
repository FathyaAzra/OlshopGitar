package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import net.fazra.olshopgitar.data.User
import net.fazra.olshopgitar.data.Cart

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userRef = database.child("users")

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                _authState.value = AuthState.Authenticated(currentUser.uid)
                _userEmail.value = currentUser.email
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _userEmail.value = user.email
                        checkIfUserExists(user)
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _userEmail.value = user.email
                        saveUserData(user)
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    private fun checkIfUserExists(user: FirebaseUser) {
        userRef.child(user.uid).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                saveUserData(user)
            } else {
                _authState.value = AuthState.Authenticated(user.uid)
                _userEmail.value = user.email
            }
        }
    }

    private fun saveUserData(user: FirebaseUser) {
        val userId = user.uid
        val email = user.email ?: "Unknown"
        _userEmail.value = email

        val newUser = User(
            userId = userId,
            email = email,
            cart = Cart(
                cartId = userId,
                items = emptyMap()
            ),
            orderHistory = emptyMap()
        )

        userRef.child(userId).setValue(newUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = AuthState.Authenticated(user.uid)
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Failed to save user data")
            }
        }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _userEmail.value = ""
    }
}

sealed class AuthState {
    data class Authenticated(val userId: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
