package ru.viafanasyev.guitarsongbook.domain.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.viafanasyev.guitarsongbook.domain.LocalSongDao
import ru.viafanasyev.guitarsongbook.domain.common.Database
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong
import ru.viafanasyev.guitarsongbook.utils.SingletonHolder2

@androidx.room.Database(entities = [LocalSong::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase(), Database<LocalSong> {
    abstract override fun songDao(): LocalSongDao

    companion object : SingletonHolder2<LocalDatabase, Context, CoroutineScope>({ context, scope ->
        Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
            "guitar-songbook.db"
        ).addCallback(
            SongDatabaseCallback(scope)
        ).build()
    })

    private class SongDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            getInstanceOrNull()?.let { database ->
                scope.launch {
                    val songDao = database.songDao()
                    songDao.insertAll(
                        *(0..30).map {
                            LocalSong("Название песни $it", "Автор $it", true, 0)
                        }.toTypedArray()
                    )
                }
            }
        }
    }
}