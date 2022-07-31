package ru.viafanasyev.guitarsongbook.domain.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.viafanasyev.guitarsongbook.domain.common.entities.ExtensionEntity
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

@Entity(tableName = "song")
data class LocalSong(
    val title: String,
    val author: String,
    val isLearned: Boolean,
    @PrimaryKey(autoGenerate = true) val id: Int,
) : ExtensionEntity<Song> {
    override fun asCommon(): Song {
        return Song(
            this.title,
            this.author,
            this.isLearned,
            this.id,
        )
    }
}