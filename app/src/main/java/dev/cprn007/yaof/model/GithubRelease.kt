package dev.cprn007.yaof.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub Release 資產（APK 下載檔）
 */
@Serializable
data class GithubAsset(
    val name: String = "",
    @SerialName("content_type")
    val contentType: String = "",
    val size: Long = 0,
    @SerialName("browser_download_url")
    val browserDownloadUrl: String = ""
)

/**
 * GitHub Releases API 回傳的最新 Release 資訊
 * GET /repos/{owner}/{repo}/releases/latest
 */
@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String = "",
    val name: String? = null,
    val body: String? = null,
    val assets: List<GithubAsset> = emptyList()
)
