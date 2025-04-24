package net.fazra.olshopgitar.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.data.Item
import net.fazra.olshopgitar.pages.components.ItemGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import net.fazra.olshopgitar.pages.components.DrawerContent
import net.fazra.olshopgitar.viewmodel.AuthState
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.ItemRepository

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    itemRepository: ItemRepository = ItemRepository()
) {
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val halfScreenWidth = screenWidth * 3 / 4
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(listOf()) }

    LaunchedEffect(Unit) {
        itemRepository.fetchCategories { fetchedCategories ->
            categories = fetchedCategories
        }
    }

    LaunchedEffect(Unit) {
        itemRepository.listenForItemsUpdates { items ->
            allItems = items
        }
    }

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    var selectedCategory by remember { mutableStateOf("All") }
    val filteredItems = allItems.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }
    val colorScheme = MaterialTheme.colorScheme

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(halfScreenWidth)
                    .fillMaxHeight()
            ) {
                DrawerContent(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    coroutineScope.launch { drawerState.open() }
                }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }

                Text(
                    text = "Olshop Gitar",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    color = colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()), // Make it scrollable horizontally
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val allCategories = listOf("All") + categories
                allCategories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Button(
                        onClick = { selectedCategory = category },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant,
                            contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(category, fontSize = 15.sp, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Items in a Grid
            ItemGrid(items = filteredItems) { item ->
                if (item.stock > 0) {
                    navController.navigate("detail/${item.id}")
                } else {
                    Toast.makeText(context, "Stok habis!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


