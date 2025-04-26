package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import net.fazra.olshopgitar.viewmodel.AuthState
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.CartViewModel
import net.fazra.olshopgitar.viewmodel.DetailViewModel
import net.fazra.olshopgitar.data.CartItem
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext


@Composable
fun DetailPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    itemId: Int,
    detailViewModel: DetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val item by detailViewModel.item.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()
    var quantity by remember { mutableIntStateOf(1) }

    // Fetch item details when the itemId changes
    LaunchedEffect(itemId) {
        detailViewModel.fetchItemById(itemId)
    }

    // Get the current authentication state and userId
    val currentUser by authViewModel.authState.observeAsState()
    val isAuthenticated = currentUser is AuthState.Authenticated
    val userId = (currentUser as? AuthState.Authenticated)?.userId

    // Display loading spinner or error message if the item is not found
    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (item == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Item tidak ditemukan")
        }
    } else {
        // Main content when the item is successfully loaded
        Column(
            modifier = modifier
                .padding(16.dp)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())  // Make the Column scrollable
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
                    text = "Produk",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Item image
            AsyncImage(
                model = item!!.photoUrl,
                contentDescription = item!!.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Item name and price
            Text(text = item!!.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Rp ${item!!.price}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))

            // Item description
            Text(text = item!!.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Item stock
            Text(text = "Stock : ${item!!.stock}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            // Quantity selection buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { if (quantity > 1) quantity-- }) {
                    Text("-")
                }

                Text(text = quantity.toString())

                Button(onClick = { if (quantity < item!!.stock) quantity++ }) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add to Cart button
            Button(
                onClick = {
                    if (isAuthenticated && userId != null) {
                        val cartItem = CartItem(
                            itemId = item!!.id.toString(),
                            name = item!!.name,
                            price = item!!.price,
                            quantity = quantity,
                            photoUrl = item!!.photoUrl
                        )

                        cartViewModel.addItemToCart(cartItem)

                        Toast.makeText(context, "Item ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        navController.navigate("login")
                    }
                },
                enabled = item!!.stock > 0
            ) {
                Text("Tambahkan ke Keranjang")
            }
        }
    }
}


