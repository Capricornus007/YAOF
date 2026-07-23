package dev.cprn007.yaof.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cprn007.yaof.R
import dev.cprn007.yaof.data.entity.AppEntity
import dev.cprn007.yaof.data.viewmodel.TrackedViewModel
import dev.cprn007.yaof.model.GithubRelease
import dev.cprn007.yaof.network.GithubApiService
import dev.cprn007.yaof.utils.ClipboardUtils
import dev.cprn007.yaof.utils.DeviceUtils
import dev.cprn007.yaof.utils.UrlParser
import kotlinx.coroutines.launch

// --------------- UI 狀態 ---------------

private sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data class Success(val release: GithubRelease) : UiState
    data class Error(val message: String) : UiState
}

/**
 * 新增來源頁面：搜尋 GitHub Release 並顯示結果
 */
@Composable
fun AddSourceScreen(
    modifier: Modifier = Modifier,
    initialRepoInput: String? = null,
    trackedViewModel: TrackedViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    var repoInput by remember { mutableStateOf(initialRepoInput ?: "") }
    var uiState by remember { mutableStateOf<UiState>(UiState.Idle) }
    var selectedProvider by remember { mutableStateOf("github") }

    // ---- 生命週期觀察：恢復焦點時檢查剪貼簿 ----
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val githubRepo = ClipboardUtils.getGithubRepoFromClipboard(context)
                if (githubRepo != null && githubRepo != repoInput) {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.clipboard_github_detected),
                            actionLabel = context.getString(R.string.clipboard_action_fill),
                            withDismissAction = true
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            repoInput = githubRepo
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ---- 輸入框 ----
        OutlinedTextField(
            value = repoInput,
            onValueChange = { newValue ->
                val parsed = UrlParser.toOwnerRepo(newValue)
                repoInput = parsed ?: newValue
            },
            label = { Text(stringResource(R.string.label_repo_input)) },
            placeholder = { Text(stringResource(R.string.add_source_placeholder)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    focusManager.clearFocus()
                    checkUpdate(scope, repoInput) { uiState = it }
                }
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---- 來源選擇 FilterChips ----
        SourceFilterChips(
            selected = selectedProvider,
            onSelect = { selectedProvider = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---- 按鈕 ----
        Button(
            onClick = {
                focusManager.clearFocus()
                checkUpdate(scope, repoInput) { uiState = it }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is UiState.Loading && repoInput.isNotBlank()
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp,
                    color = LocalContentColor.current
                )
            } else {
                Text(stringResource(R.string.btn_add_source))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---- 結果區域 ----
        when (val state = uiState) {
            is UiState.Idle -> {
                // 等待操作
            }
            is UiState.Loading -> {
                Text(
                    text = stringResource(R.string.msg_querying, repoInput),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            is UiState.Success -> {
                ReleaseResultCard(state.release)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val parsed = UrlParser.parse(repoInput) ?: return@Button
                        val app = AppEntity(
                            ownerRepo = "${parsed.first}/${parsed.second}",
                            appName = state.release.name ?: parsed.second,
                            currentVersion = state.release.tagName,
                            sourceUrl = "https://github.com/${parsed.first}/${parsed.second}",
                            providerType = "github",
                            iconUrl = "https://github.com/${parsed.first}.png"
                        )
                        trackedViewModel.addApp(app)
                        uiState = UiState.Idle
                        repoInput = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_add_to_tracking))
                }
            }
            is UiState.Error -> {
                Text(
                    text = "❌ ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Snackbar 宿主
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(8.dp)
    )
}

// --------------- 請求邏輯 ---------------

private fun checkUpdate(
    scope: kotlinx.coroutines.CoroutineScope,
    repoInput: String,
    onState: (UiState) -> Unit
) {
    val parsed = UrlParser.parse(repoInput)
    if (parsed == null) {
        onState(UiState.Error("owner/repo format required"))
        return
    }
    val (owner, repo) = parsed

    onState(UiState.Loading)
    scope.launch {
        try {
            val release = GithubApiService.getLatestRelease(owner, repo)
            if (release != null) {
                onState(UiState.Success(release))
            } else {
                onState(UiState.Error("No release found"))
            }
        } catch (e: Exception) {
            onState(UiState.Error(e.message ?: "Unknown error"))
        }
    }
}

// --------------- 結果卡片 ---------------

@Composable
private fun ReleaseResultCard(release: GithubRelease) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 版本
            Text(
                text = stringResource(R.string.label_version),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = release.name ?: release.tagName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // APK 下載（ABI 自動匹配）
            val apkAsset = DeviceUtils.pickBestApk(release.assets)
            if (apkAsset != null) {
                Text(
                    text = stringResource(R.string.label_apk_download),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = apkAsset.browserDownloadUrl,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(
                        R.string.label_file_size,
                        "%.2f".format(apkAsset.size / 1_048_576.0)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.warn_no_apk),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 更新日誌
            if (!release.body.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.label_changelog),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = release.body,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// ── 來源選擇 FilterChips ──

data class ProviderOption(val key: String, val label: String, val icon: String)

private val providerOptions = listOf(
    ProviderOption("github", "GitHub", "GH"),
    ProviderOption("gitlab", "GitLab", "GL"),
    ProviderOption("fdroid", "F-Droid", "FD"),
    ProviderOption("codeberg", "Codeberg", "CB"),
    ProviderOption("izzyondroid", "IzzyOnDroid", "IZ"),
    ProviderOption("html", "Direct URL", "🔗"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SourceFilterChips(
    selected: String,
    onSelect: (String) -> Unit
) {
    FlowRow {
        providerOptions.forEach { option ->
            FilterChip(
                selected = selected == option.key,
                onClick = { onSelect(option.key) },
                label = { Text(option.label) },
                leadingIcon = {
                    Text(
                        option.icon,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}
