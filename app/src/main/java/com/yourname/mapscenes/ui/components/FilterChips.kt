package com.yourname.mapscenes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yourname.mapscenes.model.SceneType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedType: SceneType?,
    onTypeSelected: (SceneType?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTypes = listOf(null) + SceneType.values().toList()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        allTypes.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(type?.displayName ?: "全部")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = type?.let {
                        Color(android.graphics.Color.parseColor(it.color))
                    } ?: MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}