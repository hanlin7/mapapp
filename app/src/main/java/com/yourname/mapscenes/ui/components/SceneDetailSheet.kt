package com.yourname.mapscenes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import com.yourname.mapscenes.model.Scene

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SceneDetailSheet(
    scene: Scene,
    onClose: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = scene.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 基本信息和评分
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text(scene.type.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(android.graphics.Color.parseColor(scene.type.color))
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "评分",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = scene.rating.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 详细描述
        Text(
            text = scene.detailedDescription.ifEmpty { scene.description },
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 详细信息网格
        SceneInfoGrid(scene)

        Spacer(modifier = Modifier.height(16.dp))

        // 标签
        if (scene.tags.isNotEmpty()) {
            Text(
                text = "标签",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scene.tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onToggleFavorite(scene.id) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (scene.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "收藏",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (scene.isFavorite) "已收藏" else "收藏")
            }

            Button(
                onClick = { /* 导航功能 */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = "导航")
                Spacer(modifier = Modifier.width(8.dp))
                Text("导航")
            }
        }
    }
}

@Composable
fun SceneInfoGrid(scene: Scene) {
    val items = listOfNotNull(
        if (scene.address.isNotEmpty()) InfoItem("地址", scene.address, Icons.Default.LocationOn) else null,
        if (scene.openingHours.isNotEmpty()) InfoItem("开放时间", scene.openingHours, Icons.Default.Schedule) else null,
        if (scene.ticketPrice.isNotEmpty()) InfoItem("门票", scene.ticketPrice, Icons.Default.AttachMoney) else null,
        if (scene.contactPhone.isNotEmpty()) InfoItem("电话", scene.contactPhone, Icons.Default.Phone) else null,
        if (scene.website.isNotEmpty()) InfoItem("网站", scene.website, Icons.Default.Language) else null
    )

    Column {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

data class InfoItem(
    val label: String,
    val value: String,
    val icon: ImageVector  // 修改为 ImageVector 类型
)