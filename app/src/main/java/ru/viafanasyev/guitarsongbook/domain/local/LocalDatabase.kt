package ru.viafanasyev.guitarsongbook.domain.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.viafanasyev.guitarsongbook.domain.common.Database
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong
import ru.viafanasyev.guitarsongbook.utils.SingletonHolder2

@androidx.room.Database(entities = [LocalSong::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase(), Database<LocalSong> {
    abstract override fun songDao(): LocalSongDao

    companion object : SingletonHolder2<LocalDatabase, Context, CoroutineScope>({ context, _ ->
        Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
            "guitar-songbook.db"
        ).build()
    })
}