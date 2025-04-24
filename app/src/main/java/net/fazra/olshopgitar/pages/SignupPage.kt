package net.fazra.olshopgitar.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthState
import net.fazra.olshopgitar.viewmodel.AuthViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    val context = LocalContext.current
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("home")
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text="Sign Up Page", fontSize = 34.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange ={
                email = it
            },
            label = {
                Text(text ="Email")
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange ={
                password = it
            },
            label = {
                Text(text ="Password")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                println("SignUp button clicked")
                authViewModel.signup(email, password)
            }, enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick={
            navController.navigate("login")
        }){
            Text(text = "Have an account? Login")
        }
    }
}