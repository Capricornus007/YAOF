package dev.cprn007.yaof.data.provider

/**
 * Provider 工廠：根據 URL 特徵或指定 overrideSource 分發 Provider
 */
object ProviderFactory {

    private val providers: List<UpdateProvider> = listOf(
        GitHubProvider(),
        GitLabProvider(),
        FDroidProvider(),
        HtmlFallbackProvider()
    )

    fun getProvider(sourceUrl: String, overrideSource: String? = null): UpdateProvider {
        if (overrideSource != null) {
            return providers.firstOrNull { it.type == overrideSource.lowercase() }
                ?: HtmlFallbackProvider()
        }
        return providers.firstOrNull { provider ->
            provider.hosts.any { host -> sourceUrl.contains(host, ignoreCase = true) }
        } ?: HtmlFallbackProvider()
    }

    fun getAllProviders(): List<UpdateProvider> = providers
}
