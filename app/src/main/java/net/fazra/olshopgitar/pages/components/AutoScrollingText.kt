import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AutoScrollingText(itemName: String) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(itemName) {
        while (true) {
            val maxScroll = scrollState.maxValue
            var position = 0

            // Scroll forward
            while (position < maxScroll) {
                scrollState.scrollTo(position)
                position += 2 // adjust for speed
                delay(16L) // ~60fps
            }

            // Optional pause at the end
            delay(1000)

            // Scroll back to start
            scrollState.scrollTo(0)

            // Optional pause at start
            delay(500)
        }
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = itemName,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

