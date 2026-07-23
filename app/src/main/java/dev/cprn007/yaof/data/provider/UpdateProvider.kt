package dev.cprn007.yaof.data.provider

/**
 * 更新資訊的通用資料模型
 */
data class ReleaseInfo(
    val versionName: String,
    val downloadUrl: String,
    val changeLog: String? = null,
    val iconUrl: String? = null,
    val publishDate: String? = null
)

/**
 * ObtainX 風格多來源 Provider 統一介面
 *
 * 屬性說明：
 * - hosts: 此 Provider 支援的域名列表（用於 URL 匹配）
 * - name: 顯示名稱
 * - canSearch: 是否支援搜尋功能（影響 FilterChip 顯示）
 * - sourceIdentifier: 唯一識別碼
 */
interface UpdateProvider {
    /** Provider 類型識別碼 */
    val type: String

    /** 支援的域名列表 */
    val hosts: List<String>

    /** 顯示名稱 */
    val name: String

    /** 是否支援來源內搜尋 */
    val canSearch: Boolean
        get() = false

    /** 唯一識別碼（預設為類名） */
    val sourceIdentifier: String
        get() = this::class.simpleName ?: type

    /**
     * 根據來源 URL 取得最新 Release 資訊
     * @param sourceUrl App 來源網址
     * @param apkFilterRegex 可選的 APK 過濾正則
     */
    suspend fun fetchLatest(sourceUrl: String, apkFilterRegex: String? = null): ReleaseInfo?
}

