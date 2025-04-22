package net.fazra.olshopgitar.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import net.fazra.olshopgitar.*
import net.fazra.olshopgitar.data.Item
import net.fazra.olshopgitar.pages.components.ItemGrid
import net.fazra.olshopgitar.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import net.fazra.olshopgitar.pages.components.DrawerContent

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val halfScreenWidth = screenWidth *3/4

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    val allItems = remember {
        listOf(
            Item(1, "Gitar Akustik Bernama Bento", "Gitar", 1000000, "Gitar Akustik", 3, R.drawable.guitar1),
            Item(2, "Gitar Elektrik", "Gitar", 500000, "Gitar Elektrik", 2, R.drawable.guitar1),
            Item(3, "Efek Gitar", "Effect", 300000, "Efek suara", 5, R.drawable.guitar1),
            Item(4, "Amplifier", "Amp", 700000, "Ampli kecil", 1, R.drawable.guitar1),
            Item(6, "Gitar Akustik", "Gitar", 1000000, "Gitar Akustik", 0, R.drawable.guitar1),
            Item(7, "Gitar Elektrik", "Gitar", 500000, "Gitar Elektrik", 2, R.drawable.guitar1),
            Item(8, "Efek Gitar", "Effect", 300000, "Efek suara", 0, R.drawable.guitar1),
            Item(9, "Amplifier", "Amp", 700000, "Ampli kecil", 1, R.drawable.guitar1)
        )
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
                    coroutineScope.launch {
                        drawerState.open()
                    }
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

            val categories = listOf("All", "Gitar", "Effect", "Amp")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Button(
                        onClick = { selectedCategory = category },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                colorScheme.primary
                            else
                                colorScheme.surfaceVariant,
                            contentColor = if (isSelected)
                                colorScheme.onPrimary
                            else
                                colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(category, fontSize = 15.sp, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ItemGrid(items = filteredItems) { item ->
                if (item.stock > 0) {
                    Toast.makeText(context, "Klik: ${item.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Stok habis!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}