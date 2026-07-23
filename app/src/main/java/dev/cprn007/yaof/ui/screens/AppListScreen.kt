package dev.cprn007.yaof.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.cprn007.yaof.R
import dev.cprn007.yaof.data.entity.AppEntity
import dev.cprn007.yaof.data.viewmodel.TrackedViewModel
import dev.cprn007.yaof.ui.components.AppCard

/**
 * ObtainX 風格已追蹤頁面：
 * - 搜尋列（點擊 🔍 展開）
 * - 長按進入多選模式 → 底部工具列（全選 / 刪除）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListScreen(modifier: Modifier = Modifier, viewModel: TrackedViewModel = viewModel()) {
    val apps by viewModel.apps.collectAsStateWithLifecycle()

    // 搜尋
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    // 多選模式
    val selectedIds = remember { mutableStateListOf<String>() }
    val isMultiSelectMode = selectedIds.isNotEmpty()

    // 刪除對話框
    var deleteTarget by remember { mutableStateOf<AppEntity?>(null) }
    var showBatchDeleteDialog by remember { mutableStateOf(false) }

    // 過濾
    val filteredApps = if (searchQuery.isBlank()) apps
    else apps.filter { it.appName.contains(searchQuery, ignoreCase = true) ||
                        it.ownerRepo.contains(searchQuery, ignoreCase = true) }

    Column(modifier = modifier.fillMaxSize()) {
        // ── 搜尋列 ──
        AnimatedVisibility(visible = showSearch) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    showSearch = false
                    searchQuery = ""
                }) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }
            }
        }

        // ── 列表 ──
        if (filteredApps.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_list_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps, key = { it.ownerRepo }) { app ->
                    val isSelected = app.ownerRepo in selectedIds
                    AppCard(
                        app = app,
                        isSelected = isSelected,
                        onClick = {
                            if (isMultiSelectMode) {
                                if (isSelected) selectedIds.remove(app.ownerRepo)
                                else selectedIds.add(app.ownerRepo)
                            }
                        },
                        onLongClick = {
                            if (isSelected) selectedIds.remove(app.ownerRepo)
                            else selectedIds.add(app.ownerRepo)
                        }
                    )
                }
            }
        }

        // ── 底部多選工具列 ──
        AnimatedVisibility(visible = isMultiSelectMode) {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.batch_selected_count, selectedIds.size),
                        style = MaterialTheme.typography.labelLarge
                    )
                    IconButton(onClick = {
                        if (selectedIds.size == filteredApps.size) selectedIds.clear()
                        else {
                            selectedIds.clear()
                            selectedIds.addAll(filteredApps.map { it.ownerRepo })
                        }
                    }) {
                        Icon(Icons.Filled.SelectAll, contentDescription = stringResource(R.string.action_select_all))
                    }
                    IconButton(onClick = {
                        showBatchDeleteDialog = true
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                    }
                    TextButton(onClick = { selectedIds.clear() }) {
                        Text(stringResource(R.string.delete_cancel))
                    }
                }
            }
        }
    }

    // ── 單一刪除對話框（從長按來） ──
    deleteTarget?.let { app ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_message, app.appName.ifEmpty { app.ownerRepo })) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteApp(app)
                    deleteTarget = null
                }) { Text(stringResource(R.string.delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(R.string.delete_cancel))
                }
            }
        )
    }

    // ── 批次刪除對話框 ──
    if (showBatchDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.batch_delete_message, selectedIds.size)) },
            confirmButton = {
                TextButton(onClick = {
                    val appsToDelete = apps.filter { it.ownerRepo in selectedIds }
                    appsToDelete.forEach { viewModel.deleteApp(it) }
                    selectedIds.clear()
                    showBatchDeleteDialog = false
                }) { Text(stringResource(R.string.delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDeleteDialog = false }) {
                    Text(stringResource(R.string.delete_cancel))
                }
            }
        )
    }
}



