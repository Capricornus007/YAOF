package dev.cprn007.yaof.utils

import android.net.Uri
import org.json.JSONObject
import java.net.URLDecoder

/**
 * Obtainium Deep Link 解析器
 *
 * 支援格式：
 *   obtainium://app/%7B%22name%22%3A%22...%22%7D
 *   即 URL-encoded JSON payload
 */
object ObtainiumLinkParser {

    data class ObtainiumPayload(
        val name: String?,
        val url: String?,
        val overrideSource: String?,
        val apkFilterRegEx: String?
    )

    /**
     * 嘗試解析 Obtainium deep link
     * @return 解析後的 payload，若非 obtainium:// 連結則回傳 null
     */
    fun parse(uri: Uri): ObtainiumPayload? {
        if (uri.scheme != "obtainium" || uri.host != "app") return null

        val encoded = uri.lastPathSegment ?: return null
        return try {
            val decoded = URLDecoder.decode(encoded, "UTF-8")
            val json = JSONObject(decoded)
            ObtainiumPayload(
                name = json.optString("name", "").ifEmpty { null },
                url = json.optString("url", "").ifEmpty { null },
                overrideSource = json.optString("overrideSource", "").ifEmpty { null },
                apkFilterRegEx = json.optString("apkFilterRegEx", "").ifEmpty { null }
            )
        } catch (_: Exception) {
            null
        }
    }
}
