package ru.viafanasyev.guitarsongbook.domain.local

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.viafanasyev.guitarsongbook.domain.common.SongDao
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.common.entities.ExtensionEntity
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong

class LocalSongRepository(private val songDao: SongDao<LocalSong>) : SongRepository {

    override val allLearned: Flow<List<Song>> = songDao.getAllLearned().map {
        it.map(ExtensionEntity<Song>::asCommon)
    }

    @WorkerThread
    override suspend fun getById(songId: Int): Song {
        return songDao.getById(songId).asCommon()
    }

    @WorkerThread
    override suspend fun insertAll(vararg songs: Song) {
        songDao.insertAll(*songs.map { it.asLocal() }.toTypedArray())
    }

    @WorkerThread
    override suspend fun delete(song: Song) {
        songDao.delete(song.asLocal())
    }
}