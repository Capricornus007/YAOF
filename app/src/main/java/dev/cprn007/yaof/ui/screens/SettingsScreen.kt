package dev.cprn007.yaof.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import dev.cprn007.yaof.R
import dev.cprn007.yaof.data.SettingsStore
import dev.cprn007.yaof.ui.theme.ThemeStyle
import dev.cprn007.yaof.utils.DeviceUtils
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** 語言選項 */
private enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    ZH_TW("zh-TW"),
    ZH_CN("zh-CN"),
    EN("en"),
    JA("ja")
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeStyle: ThemeStyle,
    onThemeStyleChange: (ThemeStyle) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsStore = remember { SettingsStore(context) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var abiFilterEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ---- 主題 ----
        SettingsCard(icon = Icons.Outlined.Palette, title = stringResource(R.string.settings_theme)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showThemeDialog = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = themeStyleLabel(themeStyle),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- CPU 架構 ----
        SettingsCard(icon = Icons.Outlined.Memory, title = stringResource(R.string.settings_abi)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_abi_filter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(
                            R.string.settings_abi_current,
                            DeviceUtils.supportedAbis.firstOrNull() ?: "—"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = abiFilterEnabled,
                    onCheckedChange = { abiFilterEnabled = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- 語言 ----
        SettingsCard(icon = Icons.Outlined.Language, title = stringResource(R.string.settings_language)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_language_system),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---- 關於 ----
        SettingsCard(icon = Icons.Outlined.Info, title = stringResource(R.string.settings_about)) {
            Text(
                text = stringResource(R.string.settings_about_version),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_about_github),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Capricornus007/YAOF"))
                    context.startActivity(intent)
                }
            )
        }
    }

    // ---- 主題選擇對話框 ----
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.settings_theme)) },
            text = {
                Column {
                    ThemeStyle.entries.forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onThemeStyleChange(style)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeStyle == style,
                                onClick = {
                                    onThemeStyleChange(style)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = themeStyleLabel(style),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.delete_cancel))
                }
            }
        )
    }

    // ---- 語言選擇對話框 ----
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    applyLanguage(context, lang)
                                    scope.launch { settingsStore.setLanguageTag(lang.tag) }
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = languageLabel(lang),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.delete_cancel))
                }
            }
        )
    }
}

// --------------- 主題輔助 ---------------

@Composable
private fun themeStyleLabel(style: ThemeStyle): String = when (style) {
    ThemeStyle.DYNAMIC       -> stringResource(R.string.settings_theme_dynamic)
    ThemeStyle.CLASSIC_DARK  -> stringResource(R.string.settings_theme_dark)
    ThemeStyle.OLED_BLACK    -> stringResource(R.string.settings_theme_oled)
    ThemeStyle.MORANDI_BLUE  -> stringResource(R.string.settings_theme_morandi)
    ThemeStyle.LIGHT         -> stringResource(R.string.settings_theme_light)
}

// --------------- 語言輔助 ---------------

@Composable
private fun languageLabel(lang: AppLanguage): String = when (lang) {
    AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
    AppLanguage.ZH_TW  -> stringResource(R.string.settings_language_zh_tw)
    AppLanguage.ZH_CN  -> stringResource(R.string.settings_language_zh_cn)
    AppLanguage.EN     -> stringResource(R.string.settings_language_en)
    AppLanguage.JA     -> stringResource(R.string.settings_language_ja)
}

private fun applyLanguage(context: Context, lang: AppLanguage) {
    val locales = if (lang.tag != null) {
        LocaleListCompat.forLanguageTags(lang.tag)
    } else {
        LocaleListCompat.getEmptyLocaleList()
    }
    AppCompatDelegate.setApplicationLocales(locales)
    // 觸發 Activity 重建以即時套用語言
    (context.findActivity())?.recreate()
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

// --------------- 設定卡片容器（ObtainX 風格）---------------

@Composable
private fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

