package dev.cprn007.yaof

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.cprn007.yaof.data.SettingsStore
import dev.cprn007.yaof.ui.navigation.YAOFApp
import dev.cprn007.yaof.utils.ObtainiumLinkParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private var obtainiumStartRepo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 啟動時套用已儲存的語言設定（使用 first() 一次性讀取，不阻塞）
        applySavedLocale()

        handleObtainiumIntent(intent)

        setContent {
            YAOFApp(initialRepo = obtainiumStartRepo)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleObtainiumIntent(intent)
    }

    private fun applySavedLocale() {
        val settingsStore = SettingsStore(this)
        runBlocking {
            val tag = settingsStore.languageTag.first()
            val locales = if (tag != null) {
                LocaleListCompat.forLanguageTags(tag)
            } else {
                LocaleListCompat.getEmptyLocaleList()
            }
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }

    private fun handleObtainiumIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        val payload = ObtainiumLinkParser.parse(uri)
        if (payload != null && payload.url != null) {
            obtainiumStartRepo = payload.url
        }
    }
}
