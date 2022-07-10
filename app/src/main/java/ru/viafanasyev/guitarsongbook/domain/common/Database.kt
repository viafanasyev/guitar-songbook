package ru.viafanasyev.guitarsongbook.domain.common

interface Database {
    fun songDao(): SongDao
}