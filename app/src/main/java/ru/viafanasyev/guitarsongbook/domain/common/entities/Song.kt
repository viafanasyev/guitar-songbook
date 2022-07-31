package ru.viafanasyev.guitarsongbook.domain.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.viafanasyev.guitarsongbook.domain.local.entities.LocalSong

// TODO: title+author is a unique id,
//  so need to check that there's no items with same title+author (in Add...Activity and so on)

@Parcelize
data class Song(
    val title: String,
    val author: String,
    val isLearned: Boolean,
    val id: Int = 0,
) : Parcelable, CommonEntity<LocalSong> {
    override fun asLocal(): LocalSong {
        return LocalSong(
            this.title,
            this.author,
            this.isLearned,
            this.id,
        )
    }
}