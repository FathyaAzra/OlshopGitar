package net.fazra.olshopgitar.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.CartViewModel
import net.fazra.olshopgitar.viewmodel.DetailViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource



@Composable
fun DetailPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    itemId: Int,
    detailViewModel: DetailViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    // Collect state for the item and loading status from the view model
    val item by detailViewModel.item.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()
    var quantity by remember { mutableStateOf(1) }

    // Fetch the item details when the itemId changes
    LaunchedEffect(itemId) {
        detailViewModel.fetchItemById(itemId)
    }

    // Show loading spinner while fetching data
    if (isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (item == null) {
        // Show a message if the item is not found
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Item tidak ditemukan")
        }
    } else {
        AsyncImage(
            model = item!!.photoUrl,
            contentDescription = item!!.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
        )

        Column(modifier = modifier.padding(16.dp)) {
            // Item name
            Text(text = item!!.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Rp ${item!!.price}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))

            // Item description
            Text(text = item!!.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

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

            Button(
                onClick = {
                    cartViewModel.addItemToCart(item!!, quantity)
                    navController.navigate("home")
                },
                enabled = item!!.stock > 0
            ) {
                Text("Tambahkan ke Keranjang")
            }
        }
    }
}


