package net.fazra.olshopgitar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.fazra.olshopgitar.pages.CartPage
import net.fazra.olshopgitar.pages.HomePage
import net.fazra.olshopgitar.pages.LoginPage
import net.fazra.olshopgitar.pages.SignupPage
import net.fazra.olshopgitar.pages.DetailPage
import net.fazra.olshopgitar.pages.HistoryPage
import net.fazra.olshopgitar.pages.components.TempAdd
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import net.fazra.olshopgitar.viewmodel.CartViewModel
import net.fazra.olshopgitar.viewmodel.OrderViewModel

@Composable
fun MyAppNavigation(modifier: Modifier=Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()
    val cartViewModel = CartViewModel()
    val orderViewModel = OrderViewModel()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            DetailPage(navController = navController, authViewModel = authViewModel, itemId = itemId)
        }
        composable("cart") {
            CartPage(
                modifier = Modifier,
                navController,
                cartViewModel,
                authViewModel,
                orderViewModel
            )
        }
        composable("history"){
            HistoryPage(modifier, navController, authViewModel, orderViewModel)
        }
        composable("stock"){
            TempAdd(modifier, navController)
        }
    })
}