package ru.viafanasyev.guitarsongbook.domain

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.viafanasyev.guitarsongbook.domain.common.Database
import ru.viafanasyev.guitarsongbook.domain.local.LocalSongRepository
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.local.LocalDatabase
import ru.viafanasyev.guitarsongbook.utils.Properties
import ru.viafanasyev.guitarsongbook.utils.SingletonHolder1

class DataAccessService private constructor(context: Context) {
    private val scope = CoroutineScope(SupervisorJob())
    private val database: Database<*>

    val songRepository: SongRepository

    init {
        if (Properties.USE_LOCAL_DATABASE) {
            database = LocalDatabase.getInstance(context, scope)
            songRepository = LocalSongRepository(database.songDao())
        } else {
            TODO("Remote storage is not implemented yet")
        }
    }

    companion object : SingletonHolder1<DataAccessService, Context>(::DataAccessService)
}