package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthState
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.CartViewModel
import net.fazra.olshopgitar.pages.components.CartProductCard
import net.fazra.olshopgitar.viewmodel.OrderViewModel
import net.fazra.olshopgitar.data.Order
import net.fazra.olshopgitar.data.OrderItem
import androidx.compose.foundation.layout.systemBarsPadding

@Composable
fun CartPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel
) {

    val cartItems by cartViewModel.cartItems.observeAsState(emptyMap())
    val currentUser by authViewModel.authState.observeAsState()
    val isAuthenticated = currentUser is AuthState.Authenticated
    val userId = (currentUser as? AuthState.Authenticated)?.userId.orEmpty()

    // Load cart items
    LaunchedEffect(userId) {
        if (isAuthenticated && userId.isNotEmpty()) {
            cartViewModel.loadCartFromFirebase(userId)
        }
    }

    // Check if data is loading
    if (cartItems.isEmpty()) {
        // Display loading or empty cart state
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            if (cartItems.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Your cart is empty", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("home") }) {
                        Text("Browse Products")
                    }
                }
            } else {
                CircularProgressIndicator()
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Keranjang Belanja",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                items(cartItems.values.toList()) { item ->
                    CartProductCard(cartItem = item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (userId.isNotEmpty()) {
                        val orderItemsMap = cartItems.entries.associate { entry ->
                            entry.key to OrderItem(
                                itemId = entry.value.itemId,
                                name = entry.value.name,
                                price = entry.value.price,
                                quantity = entry.value.quantity
                            )
                        }
                        val totalPrice = cartItems.values.sumOf { it.price * it.quantity }
                        val order = Order(
                            orderId = "",
                            items = orderItemsMap,
                            totalPrice = totalPrice,
                            orderDate = System.currentTimeMillis().toString()
                        )
                        orderViewModel.placeOrder(order)
                        navController.navigate("history")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Proceed to Checkout")
            }
        }
    }
}


