package ru.viafanasyev.guitarsongbook.parser

import arrow.core.Either

interface ListParser<T> {
    fun parseOne(line: String): Either<ParseError, T>
}

fun <T> ListParser<T>.parseLines(text: String): List<Either<ParseError, T>> {
    return parseAll(text.lines())
}

fun <T> ListParser<T>.parseAll(lines: List<String>): List<Either<ParseError, T>> {
    return lines.map { parseOne(it) }
}