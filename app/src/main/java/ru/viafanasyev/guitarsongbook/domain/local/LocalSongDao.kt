package ru.viafanasyev.guitarsongbook.domain.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.SongDao
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong

@Dao
interface LocalSongDao : SongDao<LocalSong> {
    @Query("SELECT id, title, author, isLearned FROM song WHERE isLearned=1")
    override fun getAllLearned(): Flow<List<SongListItem>>

    @Query("SELECT id, title, author, isLearned FROM song WHERE isLearned=0")
    override fun getAllNotLearned(): Flow<List<SongListItem>>

    @Query("SELECT * FROM song WHERE id=:songId")
    override fun getById(songId: Int): LocalSong

    @Insert
    override fun insertAll(vararg songs: LocalSong)

    @Update
    override fun update(song: LocalSong)

    @Query("UPDATE song SET isLearned=1 WHERE id=:songId ")
    override fun moveToLearned(songId: Int)

    @Query("UPDATE song SET isLearned=0 WHERE id=:songId ")
    override fun moveToNotLearned(songId: Int)

    @Query("DELETE FROM song WHERE id=:songId")
    override fun delete(songId: Int)
}