package ru.viafanasyev.guitarsongbook.domain.common

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

interface SongRepository {
    val allLearned: Flow<List<SongListItem>>

    val allNotLearned: Flow<List<SongListItem>>

    @WorkerThread
    suspend fun getById(songId: Int): Song

    @WorkerThread
    suspend fun insertAll(vararg songs: Song)

    @WorkerThread
    suspend fun update(song: Song)

    @WorkerThread
    suspend fun moveToLearned(songId: Int)

    @WorkerThread
    suspend fun moveToNotLearned(songId: Int)

    @WorkerThread
    suspend fun delete(songId: Int)
}