package dev.cprn007.yaof.utils

/**
 * 從任意字串中提取 GitHub 的 owner/repo。
 * 支援格式：
 *   - https://github.com/owner/repo
 *   - https://github.com/owner/repo/releases
 *   - https://github.com/owner/repo/releases/tag/v1.0
 *   - github.com/owner/repo（無協定）
 *   - owner/repo（直接回傳）
 */
object UrlParser {

    // 匹配 GitHub 網址並擷取 owner / repo 兩段
    private val GITHUB_URL_REGEX = Regex(
        "^(?:https?://)?(?:www\\.)?github\\.com/([^/\\s]+)/([^/\\s]+)",
        RegexOption.IGNORE_CASE
    )

    /**
     * 嘗試解析字串中的 GitHub owner/repo。
     * @return Pair(owner, repo)，若無法解析則回傳 null
     */
    fun parse(input: String): Pair<String, String>? {
        val trimmed = input.trim()

        // 先嘗試 GitHub URL 正則
        GITHUB_URL_REGEX.find(trimmed)?.let { match ->
            val owner = match.groupValues[1].trimEnd('/')
            val repo = match.groupValues[2].trimEnd('/')
            if (owner.isNotEmpty() && repo.isNotEmpty()) {
                return owner to repo
            }
        }

        // 再嘗試純 owner/repo 格式（不含 github.com）
        val parts = trimmed.split("/")
        if (parts.size == 2 && parts.none { it.isBlank() || it.contains(".") }) {
            return parts[0] to parts[1]
        }

        return null
    }

    /**
     * 轉換為標準 owner/repo 字串
     */
    fun toOwnerRepo(input: String): String? {
        return parse(input)?.let { "${it.first}/${it.second}" }
    }
}
