package net.fazra.olshopgitar.pages.components

import AutoScrollingText
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
import net.fazra.olshopgitar.data.Item

@Composable
fun ItemCard(item: Item, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val isOutOfStock = item.stock == 0
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
        Column(modifier = Modifier
            .fillMaxWidth()) {

            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = item.photoUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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

            Spacer(modifier = Modifier.height(8.dp))

            if (item.name.length > 20) {
                AutoScrollingText(item.name)
            } else {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isOutOfStock) colorScheme.onSurfaceVariant else colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )
            }


            Text(
                text = "Rp ${item.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isOutOfStock) colorScheme.onSurfaceVariant else colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
