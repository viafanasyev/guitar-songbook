package ru.viafanasyev.guitarsongbook.domain.common.entities

interface CommonEntity<LOCAL> {
    fun asLocal(): LOCAL
}