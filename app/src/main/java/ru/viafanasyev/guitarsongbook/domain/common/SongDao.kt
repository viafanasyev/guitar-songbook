package ru.viafanasyev.guitarsongbook.domain.common

import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.entities.ExtensionEntity
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

interface SongDao<T : ExtensionEntity<Song>> {
    fun getAllLearned(): Flow<List<T>>
    fun getById(songId: Int): T
    fun insertAll(vararg songs: T)
    fun update(song: T)
    fun delete(song: T)
}