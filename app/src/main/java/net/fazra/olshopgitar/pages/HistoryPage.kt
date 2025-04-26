package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.OrderViewModel
import net.fazra.olshopgitar.viewmodel.AuthState
import net.fazra.olshopgitar.pages.components.OrderItemCard

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
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (orders.isEmpty()) {
                Text(
                    text = "No orders found.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(orders) { index, order ->
                        OrderItemCard(order = order, index = index)
                    }
                }
            }
        }
    }
}

