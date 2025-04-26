package net.fazra.olshopgitar.pages.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AutoScrollingText(itemName: String) {
    val scrollState = rememberScrollState()
    LaunchedEffect(itemName) {
        while (true) {
            val maxScroll = scrollState.maxValue
            var position = 0

            while (position < maxScroll) {
                scrollState.scrollTo(position)
                position += 2
                delay(16L) // ~60fps
            }
            delay(1000)
            scrollState.scrollTo(0)
            delay(500)
        }
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = itemName
        )
    }
}

