package net.fazra.olshopgitar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.fazra.olshopgitar.pages.HomePage
import net.fazra.olshopgitar.pages.LoginPage
import net.fazra.olshopgitar.pages.SignupPage
import net.fazra.olshopgitar.pages.DetailPage
import net.fazra.olshopgitar.pages.HistoryPage
import net.fazra.olshopgitar.pages.components.TempAdd
import net.fazra.olshopgitar.viewmodel.AuthViewModel

@Composable
fun MyAppNavigation(modifier: Modifier=Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

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
//        composable("cart"){
//            CartPage(modifier, navController, authViewModel)
//            CartPage(modifier, navController, authViewModel)
//        }
        composable("history"){
            HistoryPage(modifier, navController, authViewModel)
        }
        composable("stock"){
            TempAdd(modifier, navController, authViewModel)
        }
    })
}