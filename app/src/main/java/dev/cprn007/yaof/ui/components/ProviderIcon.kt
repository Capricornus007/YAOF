package dev.cprn007.yaof.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.cprn007.yaof.R
import androidx.compose.ui.res.painterResource

/**
 * Provider 平台圖標：使用 Vector Drawable
 * 所有資源皆為標準 Android XML（相容 Android 7+）
 */
@Composable
fun ProviderIcon(
    providerType: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val resId = when (providerType.lowercase()) {
        "github"     -> R.drawable.ic_github
        "gitlab"     -> R.drawable.ic_gitlab
        "fdroid"     -> R.drawable.ic_fdroid
        "codeberg"   -> R.drawable.ic_codeberg
        "izzyondroid" -> R.drawable.ic_izzyondroid
        else         -> R.drawable.ic_direct_url
    }
    Icon(
        painter = painterResource(id = resId),
        contentDescription = providerType,
        modifier = modifier,
        tint = tint
    )
}
