package ru.viafanasyev.guitarsongbook.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.viafanasyev.guitarsongbook.domain.common.SongDao
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong

@Dao
interface LocalSongDao : SongDao<LocalSong> {
    @Query("SELECT * FROM song WHERE isLearned=1")
    override fun getAllLearned(): Flow<List<LocalSong>>

    @Query("SELECT * FROM song WHERE id=:songId")
    override fun getById(songId: Int): LocalSong

    @Insert
    override fun insertAll(vararg songs: LocalSong)

    @Delete
    override fun delete(user: LocalSong)
}