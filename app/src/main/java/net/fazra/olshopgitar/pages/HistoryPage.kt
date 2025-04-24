package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.OrderViewModel
import net.fazra.olshopgitar.data.Order
import net.fazra.olshopgitar.viewmodel.AuthState

@Composable
fun HistoryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel
) {
    val orders by orderViewModel.orderHistory.observeAsState(emptyList())
    val currentUser by authViewModel.authState.observeAsState()
    val userId = (currentUser as? AuthState.Authenticated)?.userId.orEmpty()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            orderViewModel.loadOrderHistoryFromFirebase()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Show orders once loading is complete
        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
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
                    text = "Riwayat Belanja",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (orders.isEmpty()) {
                Text(
                    text = "No orders found.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(orders) { index, order ->
                        OrderItemView(order = order, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemView(order: Order, index: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "${index + 1}. Order ID: ${order.orderId}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Date: ${order.orderDate}", style = MaterialTheme.typography.bodyMedium)

        order.items.values.forEach { item ->
            Text(text = "- ${item.name}  x${item.quantity} • Rp ${item.price}", style = MaterialTheme.typography.bodySmall)
        }

        // Display the total price of the order
        Text(text = "Total: Rp ${order.totalPrice}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

