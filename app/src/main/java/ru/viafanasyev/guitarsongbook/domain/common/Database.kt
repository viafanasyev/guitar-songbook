package ru.viafanasyev.guitarsongbook.domain.common

import ru.viafanasyev.guitarsongbook.domain.common.entities.ExtensionEntity
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

interface Database<
    SONG : ExtensionEntity<Song>
> {
    fun songDao(): SongDao<SONG>
}