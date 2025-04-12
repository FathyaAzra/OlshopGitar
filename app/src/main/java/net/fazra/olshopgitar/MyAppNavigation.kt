package net.fazra.olshopgitar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.fazra.olshopgitar.pages.LoginPage

@Composable
fun MyAppNavigation(modifier: Modifier=Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("home") {
            LoginPage(modifier, navController, authViewModel)
        }
    })
}