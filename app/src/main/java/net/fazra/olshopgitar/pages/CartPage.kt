package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import net.fazra.olshopgitar.viewmodel.CartViewModel

@Composable
fun CartPage(
    cartViewModel: CartViewModel) {
    val cartItems = cartViewModel.cartItems

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Keranjang Belanja", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Text(text = "Keranjang kosong", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(cartItems) { item ->
                    Text(text = "${item.name} - Qty: ${item.quantity}")
                }
            }
        }
    }
}
