package ru.viafanasyev.guitarsongbook.domain.common.entities

interface ExtensionEntity<COMMON> {
    fun asCommon(): COMMON
}