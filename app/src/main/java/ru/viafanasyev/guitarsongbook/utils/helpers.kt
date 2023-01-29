package ru.viafanasyev.guitarsongbook.utils

import arrow.core.Either

fun <A, B> List<Either<A, B>>.unzipEither(): Pair<List<A>, List<B>> {
    val lefts = mutableListOf<A>()
    val rights = mutableListOf<B>()
    this.forEach {
        when (it) {
            is Either.Left<A> -> lefts.add(it.value)
            is Either.Right<B> -> rights.add(it.value)
        }
    }
    return Pair(lefts, rights)
}