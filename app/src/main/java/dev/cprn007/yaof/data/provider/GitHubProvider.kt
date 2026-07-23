package dev.cprn007.yaof.data.provider

import dev.cprn007.yaof.network.GithubApiService
import dev.cprn007.yaof.utils.DeviceUtils
import dev.cprn007.yaof.utils.UrlParser

/**
 * GitHub Releases Provider
 */
class GitHubProvider : UpdateProvider {
    override val type = "github"
    override val hosts = listOf("github.com")
    override val name = "GitHub"
    override val canSearch = true

    override suspend fun fetchLatest(sourceUrl: String, apkFilterRegex: String?): ReleaseInfo? {
        val parsed = UrlParser.parse(sourceUrl) ?: return null
        val release = GithubApiService.getLatestRelease(parsed.first, parsed.second) ?: return null
        val apk = DeviceUtils.pickBestApk(release.assets) ?: return null

        val iconUrl = "https://github.com/${parsed.first}.png"

        return ReleaseInfo(
            versionName = release.name ?: release.tagName,
            downloadUrl = apk.browserDownloadUrl,
            changeLog = release.body,
            iconUrl = iconUrl,
            publishDate = null
        )
    }
}
