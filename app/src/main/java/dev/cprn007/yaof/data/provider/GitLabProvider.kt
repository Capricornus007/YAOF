package dev.cprn007.yaof.data.provider

/**
 * GitLab Releases Provider（骨架，待實作）
 */
class GitLabProvider : UpdateProvider {
    override val type = "gitlab"
    override val hosts = listOf("gitlab.com")
    override val name = "GitLab"

    override suspend fun fetchLatest(sourceUrl: String, apkFilterRegex: String?): ReleaseInfo? {
        // TODO: 實作 GitLab API 查詢
        return null
    }
}
