package ru.viafanasyev.guitarsongbook.domain.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongListItem(
    val title: String,
    val author: String,
    val isLearned: Boolean,
    val id: Int = 0,
) : Parcelable