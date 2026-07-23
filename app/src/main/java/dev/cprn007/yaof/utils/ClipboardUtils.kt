package dev.cprn007.yaof.utils

import android.content.ClipboardManager
import android.content.Context
import android.os.Build

/**
 * 剪貼簿工具：讀取系統剪貼簿內容
 */
object ClipboardUtils {

    /**
     * 從剪貼簿讀取文字，若內容為 GitHub 網址則回傳解析後的 owner/repo。
     * Android 10+ 只有在前景 App 才能讀取剪貼簿。
     */
    fun getGithubRepoFromClipboard(context: Context): String? {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            ?: return null

        // Android 10+ 限制背景讀取剪貼簿
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !cm.hasPrimaryClip()) {
            return null
        }

        val clip = cm.primaryClip ?: return null
        if (clip.itemCount == 0) return null

        val text = clip.getItemAt(0).text?.toString() ?: return null
        return UrlParser.toOwnerRepo(text)
    }
}
