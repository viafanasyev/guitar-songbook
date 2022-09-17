package ru.viafanasyev.guitarsongbook.domain.local

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.SongDao
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong

class LocalSongRepository(private val songDao: SongDao<LocalSong>) : SongRepository {

    override val allLearned: Flow<List<SongListItem>> = songDao.getAllLearned()

    override val allNotLearned: Flow<List<SongListItem>> = songDao.getAllNotLearned()

    @WorkerThread
    override suspend fun getById(songId: Int): Song {
        return songDao.getById(songId).asCommon()
    }

    @WorkerThread
    override suspend fun insertAll(vararg songs: Song) {
        songDao.insertAll(*songs.map { it.asLocal() }.toTypedArray())
    }

    @WorkerThread
    override suspend fun update(song: Song) {
        songDao.update(song.asLocal())
    }

    @WorkerThread
    override suspend fun moveToLearned(songId: Int) {
        songDao.moveToLearned(songId)
    }

    @WorkerThread
    override suspend fun moveToNotLearned(songId: Int) {
        songDao.moveToNotLearned(songId)
    }

    @WorkerThread
    override suspend fun delete(songId: Int) {
        songDao.delete(songId)
    }
}