package net.fazra.olshopgitar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.fazra.olshopgitar.data.Item

class DetailViewModel : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchItemById(itemId: Int) {
        _isLoading.value = true
        val dbRef = FirebaseDatabase.getInstance().getReference("items/$itemId")
        dbRef.get().addOnSuccessListener { snapshot ->
            _item.value = snapshot.getValue(Item::class.java)
            _isLoading.value = false
        }.addOnFailureListener {
            _item.value = null
            _isLoading.value = false
        }
    }
}
