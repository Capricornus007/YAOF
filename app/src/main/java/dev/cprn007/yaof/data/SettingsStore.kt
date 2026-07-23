package dev.cprn007.yaof.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val KEY_LANGUAGE_TAG = stringPreferencesKey("language_tag")
    }

    /** 讀取已儲存的語言標籤，null 表示跟隨系統 */
    val languageTag: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE_TAG]
    }

    /** 寫入語言標籤 */
    suspend fun setLanguageTag(tag: String?) {
        context.dataStore.edit { prefs ->
            if (tag != null) {
                prefs[KEY_LANGUAGE_TAG] = tag
            } else {
                prefs.remove(KEY_LANGUAGE_TAG)
            }
        }
    }
}
