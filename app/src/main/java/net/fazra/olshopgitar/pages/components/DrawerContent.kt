package net.fazra.olshopgitar.pages.components

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import net.fazra.olshopgitar.viewmodel.AuthViewModel

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    Column(modifier.padding(16.dp)) {
        Text(
            text = "Olshop Gitar",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        DrawerItem(
            icon = Icons.Default.ShoppingCart,
            label = "Keranjang",
            onClick = { navController.navigate("cart") }
        )

        DrawerItem(
            icon = Icons.Default.Refresh,
            label = "Riwayat Belanja",
            onClick = { navController.navigate("history") }
        )

        DrawerItem(
            icon = Icons.Default.ExitToApp,
            label = "Sign out",
            onClick = {
                authViewModel.signout()
                Toast.makeText(context, "Berhasil Signout", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )

        DrawerItem(
            icon = Icons.Default.ShoppingCart,
            label = "Stock Change (Khusus Admin/testing)",
            onClick = { navController.navigate("stock") }
        )
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
