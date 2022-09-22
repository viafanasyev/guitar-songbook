package ru.viafanasyev.guitarsongbook.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

class SongListParser(
    private val matcher: SongMatcher,
    private val asLearned: Boolean,
) : ListParser<Song> {
    override fun parseOne(line: String): Either<ParseError, Song> {
        val (title, author) = matcher.match(line) ?: return ParseError(line).left()
        if (title.isEmpty() || author.isEmpty()) {
            return ParseError(line).left()
        }
        return Song(title, author, asLearned).right()
    }
}