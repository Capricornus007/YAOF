package dev.cprn007.yaof.data.provider

/**
 * F-Droid Provider（骨架，待實作）
 */
class FDroidProvider : UpdateProvider {
    override val type = "fdroid"
    override val hosts = listOf("f-droid.org")
    override val name = "F-Droid"

    override suspend fun fetchLatest(sourceUrl: String, apkFilterRegex: String?): ReleaseInfo? {
        // TODO: 解析 F-Droid 頁面或 API
        return null
    }
}
