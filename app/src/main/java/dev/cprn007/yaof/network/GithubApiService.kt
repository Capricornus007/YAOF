package dev.cprn007.yaof.network

import dev.cprn007.yaof.model.GithubRelease
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * GitHub API 服務：抓取最新 Release 資訊
 */
object GithubApiService {

    private const val BASE_URL = "https://api.github.com"

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }
    }

    /**
     * 取得指定 repo 的最新 Release（含 Pre-release / Nightly）
     * @param owner 倉庫擁有者
     * @param repo  倉庫名稱
     * @return GithubRelease?，無任何 Release 時回傳 null
     */
    suspend fun getLatestRelease(owner: String, repo: String): GithubRelease? {
        return client.get("$BASE_URL/repos/$owner/$repo/releases") {
            header("Accept", "application/vnd.github+json")
            header("User-Agent", "YAOF-UpdateChecker")
            header("X-GitHub-Api-Version", "2022-11-28")
            url {
                parameters.append("per_page", "1")
            }
        }.body<List<GithubRelease>>().firstOrNull()
    }
}
