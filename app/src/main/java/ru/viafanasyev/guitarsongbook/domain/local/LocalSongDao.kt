package ru.viafanasyev.guitarsongbook.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.SongDao
import ru.viafanasyev.guitarsongbook.domain.entities.Song

@Dao
interface LocalSongDao : SongDao {
    @Query("SELECT * FROM song WHERE isKnown=1")
    override fun getAllKnown(): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE id=:songId")
    override fun getById(songId: Int): Song

    @Insert
    override fun insertAll(vararg songs: Song)

    @Delete
    override fun delete(user: Song)
}