package dev.cprn007.yaof.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import dev.cprn007.yaof.R
import dev.cprn007.yaof.ui.screens.AddSourceScreen
import dev.cprn007.yaof.ui.screens.AppListScreen
import dev.cprn007.yaof.ui.screens.SettingsScreen
import dev.cprn007.yaof.ui.theme.ThemeStyle
import dev.cprn007.yaof.ui.theme.YAOFTheme

/**
 * 底部導覽頁籤定義
 */
enum class Screen(
    val labelResId: Int,
    val titleResId: Int,
    val icon: ImageVector
) {
    AppList(R.string.tab_app_list, R.string.title_tracked, Icons.Filled.Apps),
    AddSource(R.string.tab_add_source, R.string.title_add_source, Icons.Filled.Add),
    Settings(R.string.tab_settings, R.string.title_settings, Icons.Filled.Settings)
}

/**
 * 主應用殼層：TopAppBar + 底部導覽列 + 頁面切換
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YAOFApp(initialRepo: String? = null) {
    var currentScreen by remember { mutableStateOf(Screen.AddSource) }
    var themeStyle by remember { mutableStateOf(ThemeStyle.DYNAMIC) }

    YAOFTheme(themeStyle = themeStyle) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(currentScreen.titleResId),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelResId)) },
                            label = { Text(stringResource(screen.labelResId)) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (currentScreen) {
                Screen.AppList   -> AppListScreen(modifier)
                Screen.AddSource -> AddSourceScreen(
                    modifier = modifier,
                    initialRepoInput = initialRepo
                )
                Screen.Settings  -> SettingsScreen(
                    modifier = modifier,
                    themeStyle = themeStyle,
                    onThemeStyleChange = { themeStyle = it }
                )
            }
        }
    }
}
