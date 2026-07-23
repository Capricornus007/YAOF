package dev.cprn007.yaof.utils

import android.os.Build
import dev.cprn007.yaof.model.GithubAsset

/**
 * 裝置資訊工具：ABI 架構匹配
 */
object DeviceUtils {

    /** 當前裝置支援的 ABI 列表（優先序從高到低） */
    val supportedAbis: List<String> by lazy {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS.toList()
        } else {
            listOf(Build.CPU_ABI, Build.CPU_ABI2).filter { it.isNotEmpty() }
        }
    }

    /**
     * 從 Release 的資產列表中，根據裝置 ABI 優先挑選最佳 APK。
     *
     * 匹配優先級：
     *   1. 檔名包含裝置 ABI（如 arm64-v8a）
     *   2. 檔名包含 "universal" 或 "noarch"
     *   3. 任意 .apk 檔案
     *
     * @return 最佳匹配的 GithubAsset，無任何 APK 時回傳 null
     */
    fun pickBestApk(assets: List<GithubAsset>): GithubAsset? {
        val apks = assets.filter { it.name.endsWith(".apk", ignoreCase = true) }
        if (apks.isEmpty()) return null

        // 1. 精確 ABI 匹配（按 SUPPORTED_ABIS 優先序）
        for (abi in supportedAbis) {
            apks.firstOrNull { apk ->
                apk.name.contains(abi, ignoreCase = true)
            }?.let { return it }
        }

        // 2. Universal / Noarch
        apks.firstOrNull { apk ->
            apk.name.contains("universal", ignoreCase = true) ||
            apk.name.contains("noarch", ignoreCase = true)
        }?.let { return it }

        // 3. 退回第一個 APK
        return apks.first()
    }
}
