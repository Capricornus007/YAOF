package dev.cprn007.yaof.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.cprn007.yaof.data.db.AppDatabase
import dev.cprn007.yaof.data.entity.AppEntity
import dev.cprn007.yaof.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    /** 來自 Room DB 的即時追蹤列表 */
    val apps: StateFlow<List<AppEntity>>

    init {
        val dao = AppDatabase.getInstance(application).appDao()
        repository = AppRepository(dao)
        apps = repository.allApps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    }

    /** 真實從 DB 刪除 */
    fun deleteApp(app: AppEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteApp(app)
        }
    }

    /** 新增追蹤 */
    fun addApp(app: AppEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertApp(app)
        }
    }
}
