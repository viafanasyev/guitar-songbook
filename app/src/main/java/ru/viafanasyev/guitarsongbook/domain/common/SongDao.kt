package ru.viafanasyev.guitarsongbook.domain.common

import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.entities.Song

interface SongDao {
    fun getAllLearned(): Flow<List<Song>>
    fun getById(songId: Int): Song
    fun insertAll(vararg songs: Song)
    fun delete(user: Song)
}