package ru.viafanasyev.guitarsongbook.domain.common

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.entities.Song

class SongRepository(private val songDao: SongDao) {

    val allKnown: Flow<List<Song>> = songDao.getAllKnown()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getById(songId: Int): Song {
        return songDao.getById(songId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(vararg songs: Song) {
        songDao.insertAll(*songs)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(user: Song) {
        songDao.delete(user)
    }
}