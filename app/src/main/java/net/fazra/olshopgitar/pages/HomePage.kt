package net.fazra.olshopgitar.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.AuthState
import net.fazra.olshopgitar.AuthViewModel
import net.fazra.olshopgitar.data.Item
import net.fazra.olshopgitar.pages.components.ItemCard

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login")
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    // Example data (replace with real ViewModel or repository data)
    val allItems = remember {
        listOf(
            /*TODO: Photo Adding*/
            Item("Gitar Akustik A1", "Akustik", 1000000, "Sebuah gitar akustik", 3, 1),
            Item("Gitar Elektrik E1", "Elektrik", 50000, "Sebuah gitar listrik", 3, 2),
            Item("Efek Zoom G1", "Effect", 1000000, "Sebuah Effect", 5, 3),
            Item("Amplifier Vox", "Amp", 1000000, "Sebuah Amplifier", 2, 4),
            Item("10 Pick", "Lainnya", 1000000, "Sebuah gitar akustik", 3, 5)
        )
    }

    var selectedCategory by remember { mutableStateOf("All") }
    val itemsToShow = allItems.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Olshop Gitar", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Filter buttons
        val categories = listOf("All", "Lainnya", "Akustik", "Elektrik", "Effect", "Amp")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categories.forEach { category ->
                Button(
                    onClick = { selectedCategory = category },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == category) Color.Blue else Color.Gray
                    )
                ) {
                    Text(text = category)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of item cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itemsToShow.size) { index ->
                val item = itemsToShow[index]
                ItemCard(item)
            }
        }
    }
}
