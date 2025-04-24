package net.fazra.olshopgitar.pages.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.fazra.olshopgitar.data.CartItem

@Composable
fun CartProductCard(cartItem: CartItem, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val isOutOfStock = cartItem.quantity == 0
    val colorScheme = MaterialTheme.colorScheme

    val cardModifier = if (onClick != null && !isOutOfStock) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOutOfStock) {
                if (isSystemInDarkTheme()) {
                    colorScheme.surfaceVariant.copy(alpha = 0.4f)
                } else {
                    colorScheme.surfaceVariant
                }
            } else {
                colorScheme.surface
            }
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = cartItem.photoUrl,
                    contentDescription = cartItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                )

                if (isOutOfStock) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(8.dp),
                        color = colorScheme.error.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Stok Habis",
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                            color = colorScheme.onError,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Product Information Section
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Product Name
                Text(
                    text = cartItem.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isOutOfStock) colorScheme.onSurfaceVariant else colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                // Product Price
                Text(
                    text = "Rp ${cartItem.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOutOfStock) colorScheme.onSurfaceVariant else colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quantity Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Qty: ${cartItem.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}
