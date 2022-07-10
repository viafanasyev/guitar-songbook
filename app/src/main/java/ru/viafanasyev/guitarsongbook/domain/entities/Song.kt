package ru.viafanasyev.guitarsongbook.domain.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Song(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String,
    val isKnown: Boolean,
) : Parcelable