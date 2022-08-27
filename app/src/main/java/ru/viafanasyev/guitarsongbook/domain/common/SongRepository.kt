package ru.viafanasyev.guitarsongbook.domain.common

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

interface SongRepository {
    val allLearned: Flow<List<Song>>

    @WorkerThread
    suspend fun getById(songId: Int): Song

    @WorkerThread
    suspend fun insertAll(vararg songs: Song)

    @WorkerThread
    suspend fun update(song: Song)

    @WorkerThread
    suspend fun delete(song: Song)
}