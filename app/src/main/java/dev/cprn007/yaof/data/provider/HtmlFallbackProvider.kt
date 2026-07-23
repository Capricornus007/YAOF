package dev.cprn007.yaof.data.provider

/**
 * HTML 頁面解析 Provider（骨架，待實作 Jsoup 解析）
 */
class HtmlFallbackProvider : UpdateProvider {
    override val type = "html"
    override val hosts: List<String> = emptyList()
    override val name = "Direct URL"

    override suspend fun fetchLatest(sourceUrl: String, apkFilterRegex: String?): ReleaseInfo? {
        // TODO: 使用 Jsoup 抓取 HTML，正則匹配 .apk 直鏈
        return null
    }
}
