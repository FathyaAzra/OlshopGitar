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

@Composable
fun CartPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    // Use observeAsState for LiveData to get the cart items
    val cartItems by cartViewModel.cartItems.observeAsState(initial = emptyList())

    // Observe authentication state
    val currentUser by authViewModel.authState.observeAsState()

    val isAuthenticated = currentUser is AuthState.Authenticated
    val userId = (currentUser as? AuthState.Authenticated)?.userId.orEmpty()

    var isLoading by remember { mutableStateOf(true) }

    // Trigger loading cart items when the user is authenticated
    LaunchedEffect(userId) {
        if (isAuthenticated && userId.isNotEmpty()) {
            cartViewModel.loadCartFromFirebase(userId)
            isLoading = false
        } else {
            isLoading = false
        }
    }

    // Show loading spinner while data is being loaded
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Main content for the cart page
        Column(modifier = modifier.padding(10.dp)) {
            // Header with back button and title
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

            // Cart content or empty state
            if (cartItems.isEmpty()) {
                Text(
                    text = "Keranjang kosong",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Display cart items
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(cartItems) { item ->
                        CartProductCard(cartItem = item)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Proceed to checkout button
                Button(
                    onClick = { navController.navigate("shopping_history") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Proceed to Checkout")
                }
            }
        }
    }

    // Show message to login if the user is not authenticated
    if (!isAuthenticated) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You need to login to view your cart.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("login") }) {
                    Text(text = "Login")
                }
            }
        }
    }
}
