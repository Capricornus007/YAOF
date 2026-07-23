package dev.cprn007.yaof.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── OLED 純黑 ──
private val OLEDBlackScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFCE93D8),
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceVariant = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0)
)

// ── 莫蘭迪藍靜態 ──
private val MorandiBlueScheme = lightColorScheme(
    primary = Color(0xFF6B7B8D),
    secondary = Color(0xFF8E9AAF),
    tertiary = Color(0xFFA3B5C8),
    background = Color(0xFFF0F2F5),
    surface = Color(0xFFE8ECF0),
    surfaceVariant = Color(0xFFDFE3E8),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF2C3A47),
    onSurface = Color(0xFF2C3A47),
    onSurfaceVariant = Color(0xFF5A6B7D)
)

private val MorandiBlueDarkScheme = darkColorScheme(
    primary = Color(0xFF8E9AAF),
    secondary = Color(0xFFA3B5C8),
    tertiary = Color(0xFFB8C9DD),
    background = Color(0xFF1A1D23),
    surface = Color(0xFF22252C),
    surfaceVariant = Color(0xFF2D3138),
    onPrimary = Color(0xFF1A1D23),
    onBackground = Color(0xFFE0E4EA),
    onSurface = Color(0xFFE0E4EA),
    onSurfaceVariant = Color(0xFFA3ADBB)
)

// ── 經典暗黑 ──
private val ClassicDarkScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// ── 經典淺色 ──
private val ClassicLightScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/** 主題樣式 */
enum class ThemeStyle {
    DYNAMIC,
    CLASSIC_DARK,
    OLED_BLACK,
    MORANDI_BLUE,
    LIGHT
}

@Composable
fun YAOFTheme(
    themeStyle: ThemeStyle = ThemeStyle.DYNAMIC,
    content: @Composable () -> Unit
) {
    val colorScheme = resolveColorScheme(themeStyle)

    val view = LocalView.current
    val darkTheme = colorScheme.background.luminance() < 0.5f
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun resolveColorScheme(style: ThemeStyle): ColorScheme {
    val darkSystem = isSystemInDarkTheme()
    val context = LocalContext.current

    return when (style) {
        ThemeStyle.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkSystem) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkSystem) ClassicDarkScheme else ClassicLightScheme
            }
        }
        ThemeStyle.CLASSIC_DARK -> ClassicDarkScheme
        ThemeStyle.OLED_BLACK    -> OLEDBlackScheme
        ThemeStyle.MORANDI_BLUE  -> if (darkSystem) MorandiBlueDarkScheme else MorandiBlueScheme
        ThemeStyle.LIGHT         -> ClassicLightScheme
    }
}

private fun Color.luminance(): Float {
    val r = red / 255f
    val g = green / 255f
    val b = blue / 255f
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}