package ru.viafanasyev.guitarsongbook.parser

import arrow.core.Either
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

class SongListParserTest {
    @Test
    fun parseOne_singleCorrectLine_songCorrectlyParsedExpected() {
        val asLearned = true
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val input = "$inputAuthor - $inputTitle"

        val result = parser.parseOne(input)

        assertRight(inputAuthor, inputTitle, asLearned, result)
    }

    @Test
    fun parseOne_singleCorrectLineAsNotLearned_songCorrectlyParsedExpected() {
        val asLearned = false
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val input = "$inputAuthor - $inputTitle"

        val result = parser.parseOne(input)

        assertRight(inputAuthor, inputTitle, asLearned, result)
    }

    @Test
    fun parseOne_invalidLine_errorWithInputLineExpected() {
        val asLearned = true
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val input = "__=+ $inputAuthor _+++_ $inputTitle __=+"

        val result = parser.parseOne(input)

        assertLeft(ParseError(input), result)
    }

    @Test
    fun parseLines_allLinesAreCorrect_allLinesParsedExpected() {
        val asLearned = true
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val input = """
            $inputAuthor - $inputTitle
            $inputAuthor     -    $inputTitle
                   $inputAuthor    -       $inputTitle       
        """.trimIndent()

        val result = parser.parseLines(input)
        for (singleResult in result) {
            assertRight(inputAuthor, inputTitle, asLearned, singleResult)
        }
    }

    @Test
    fun parseLines_someLinesAreCorrect_onlyCorrectLinesAreParsedExpected() {
        val asLearned = true
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val badLine = "$inputAuthor -+- $inputTitle"
        val input = """
            $inputAuthor - $inputTitle
            $badLine
            $inputAuthor - $inputTitle
            $badLine
        """.trimIndent()

        val (rightResults, leftResults) = parser.parseLines(input).partition { it.isRight() }

        assertEquals(2, rightResults.size)
        for (singleResult in rightResults) {
            assertRight(inputAuthor, inputTitle, asLearned, singleResult)
        }
        assertEquals(2, leftResults.size)
        for (singleResult in leftResults) {
            assertLeft(ParseError(badLine), singleResult)
        }
    }

    @Test
    fun parseLines_someLinesAreCorrect_correctOrderOfCorrectAndIncorrectLinesExpected() {
        val asLearned = true
        val parser = SongListParser(
            matcher = SongMatcher.newInstance(true, "-"),
            asLearned = asLearned
        )
        val inputAuthor = "Author"
        val inputTitle = "Title"
        val input = """
            ${inputAuthor}1 - ${inputTitle}1
            123 123123 1231 231 $inputAuthor     -    $inputTitle
            $inputAuthor $inputTitle
            ${inputAuthor}2 - ${inputTitle}2
            ${inputAuthor}3 - ${inputTitle}3
        """.trimIndent()

        val (rightResults, leftResults) = parser.parseLines(input).partition { it.isRight() }

        assertEquals(3, rightResults.size)
        assertRight("${inputAuthor}1", "${inputTitle}1", asLearned, rightResults[0])
        assertRight("${inputAuthor}2", "${inputTitle}2", asLearned, rightResults[1])
        assertRight("${inputAuthor}3", "${inputTitle}3", asLearned, rightResults[2])

        assertEquals(2, leftResults.size)
        assertLeft(ParseError("123 123123 1231 231 $inputAuthor     -    $inputTitle"), leftResults[0])
        assertLeft(ParseError("$inputAuthor $inputTitle"), leftResults[1])
    }

    private fun assertRight(expectedAuthor: String, expectedTitle: String, expectedLearned: Boolean, result: Either<ParseError, Song>) {
        assertTrue(result.isRight())
        result.tap {
            assertEquals(expectedAuthor, it.author)
            assertEquals(expectedTitle, it.title)
            assertEquals(expectedLearned, it.isLearned)
        }
    }

    private fun assertLeft(expectedError: ParseError, result: Either<ParseError, Song>) {
        assertTrue(result.isLeft())
        result.tapLeft {
            assertEquals(expectedError, it)
        }
    }
}