package net.fazra.olshopgitar.pages.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.fazra.olshopgitar.data.Item

@Composable
fun ItemGrid(
    items: List<Item>,
    onItemClick: (Item) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
                onClick = if (item.stock > 0) {
                    { onItemClick(item) }
                } else null
            )
        }
    }
}
