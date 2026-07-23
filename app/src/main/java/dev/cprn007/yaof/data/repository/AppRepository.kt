package dev.cprn007.yaof.data.repository

import dev.cprn007.yaof.data.dao.AppDao
import dev.cprn007.yaof.data.entity.AppEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allApps: Flow<List<AppEntity>> = appDao.getAllApps()

    suspend fun insertApp(app: AppEntity) {
        appDao.insertApp(app)
    }

    suspend fun deleteApp(app: AppEntity) {
        appDao.deleteApp(app)
    }

    suspend fun getApp(id: String): AppEntity? {
        return appDao.getApp(id)
    }
}
