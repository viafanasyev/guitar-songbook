package ru.viafanasyev.guitarsongbook.ui.actions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SongActionType {
    SONG_EDIT,
    MOVE_SONG_TO_LEARNED,
    MOVE_SONG_TO_NOT_LEARNED,
}

@Parcelize
data class SongAction(val actionType: SongActionType, val layoutResId: Int) : Parcelable