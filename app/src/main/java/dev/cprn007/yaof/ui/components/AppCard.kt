package dev.cprn007.yaof.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.cprn007.yaof.data.entity.AppEntity

/**
 * ObtainX 風格追蹤卡片：
 * - 左側 App 圖示 + 右下角來源標籤
 * - 中間 App 名稱 + "由 owner" 副標
 * - 右側版本號膠囊
 * - 選中態：主色邊框 + 圖示替換為 ✓
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppCard(
    app: AppEntity,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val border = if (isSelected) {
        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = border
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── 左側：圖示 + 來源標籤疊加 ──
            Box(contentAlignment = Alignment.BottomEnd) {
                if (isSelected) {
                    // 選中態：主色圓形 + ✓ 取代圖示
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else {
                    AsyncImage(
                        model = app.iconUrl ?: defaultIconUrl(app),
                        contentDescription = app.appName,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // 來源小標籤
                    ProviderBadge(
                        providerType = app.providerType,
                        modifier = Modifier.offset(x = 3.dp, y = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ── 中間：名稱 + 作者 ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName.ifEmpty { app.ownerRepo },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.ownerRepo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── 右側：版本膠囊 ──
            if (!app.currentVersion.isNullOrEmpty()) {
                VersionChip(version = app.currentVersion)
            }
        }
    }
}

// ── 版本膠囊 ──

@Composable
private fun VersionChip(version: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = version,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1
        )
    }
}

// ── Provider 來源標籤 ──

@Composable
private fun ProviderBadge(providerType: String, modifier: Modifier = Modifier) {
    val bgColor = when (providerType.lowercase()) {
        "github"  -> Color(0xFF24292F)
        "gitlab"  -> Color(0xFFFC6D26)
        "fdroid"  -> Color(0xFF1976D2)
        "codeberg" -> Color(0xFF2185D0)
        "izzyondroid" -> Color(0xFF3F51B5)
        else      -> Color.Gray
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        ProviderIcon(
            providerType = providerType,
            modifier = Modifier
                .size(14.dp)
                .padding(1.dp),
            tint = Color.White
        )
    }
}

// ── 預設圖示 ──

private fun defaultIconUrl(app: AppEntity): String? {
    return when (app.providerType.lowercase()) {
        "github" -> {
            val owner = app.ownerRepo.substringBefore("/")
            "https://github.com/$owner.png"
        }
        else -> null
    }
}

