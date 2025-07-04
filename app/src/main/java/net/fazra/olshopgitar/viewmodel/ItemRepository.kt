package net.fazra.olshopgitar.viewmodel

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.fazra.olshopgitar.data.Item

class ItemRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val itemsRef: DatabaseReference = database.getReference("items")

    fun fetchCategories(callback: (List<String>) -> Unit) {
        itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableSetOf<String>()

                for (itemSnapshot in snapshot.children) {
                    val category = itemSnapshot.child("category").getValue(String::class.java)
                    if (!category.isNullOrEmpty()) {
                        categories.add(category)
                    }
                }

                callback(categories.toList())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // Listen for real-time updates
    fun listenForItemsUpdates(callback: (List<Item>) -> Unit) {
        itemsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<Item>()
                for (child in snapshot.children) {
                    val item = child.getValue(Item::class.java)
                    if (item != null) {
                        itemsList.add(item)
                    }
                }
                callback(itemsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

