package ru.viafanasyev.guitarsongbook.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SongMatcherTest {
    @Test
    fun authorBeforeTitleMatch_correctLine_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-")

        assertMatches("Author", "Title", songMatcher, "Author - Title")
        assertMatches("Author", "Title", songMatcher, "Author-Title")
        assertMatches("Author", "Title", songMatcher, "Author\t\t-\n\nTitle")
        assertMatches("Author", "Title", songMatcher, "     Author   -   Title    ")
        assertMatches("Author", "Title", songMatcher, "\n\r\t     Author \n\r\t  - \n\r\t  Title  \n\r\t  ")
    }

    @Test
    fun authorBeforeTitleMatch_titleUsesSeparator_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-", titlePattern = """(\w|-)+""")

        assertMatches("Author", "Ti-tle", songMatcher, "Author - Ti-tle")
    }

    @Test
    fun authorBeforeTitleMatch_authorUsesSeparator_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-", authorPattern = """(\w|-)+""")

        assertMatches("A-ut-hor", "Title", songMatcher, "A-ut-hor - Title")
    }

    @Test
    fun titleBeforeAuthorMatch_correctLine_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(false, "-")

        assertMatches("Author", "Title", songMatcher, "Title - Author")
        assertMatches("Author", "Title", songMatcher, "Title-Author")
        assertMatches("Author", "Title", songMatcher, "Title\t\t-\n\nAuthor")
        assertMatches("Author", "Title", songMatcher, "     Title   -   Author    ")
    }

    @Test
    fun authorBeforeTitleMatch_correctLineWithEmptySeparator_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "")

        assertMatches("Author", "Title", songMatcher, "Author Title")
        assertMatches("Author", "Title", songMatcher, "Author\t\t\n\nTitle")
        assertMatches("Author", "Title", songMatcher, "     Author      Title    ")
    }

    @Test
    fun authorBeforeTitleMatch_lineWithWrongSeparator_notMatchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-")

        assertNotMatches(songMatcher, "Author + Title")
        assertNotMatches(songMatcher, "Author -- Title")
        assertNotMatches(songMatcher, "Author Title")
    }

    @Test
    fun authorBeforeTitleMatch_lineWithGarbagePrefixAndSuffix_notMatchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-")

        assertNotMatches(songMatcher, "- Author - Title")
        assertNotMatches(songMatcher, "Author - Title -")
        assertNotMatches(songMatcher, "- Author - Title -")
        assertNotMatches(songMatcher, "Author Author - Title")
        assertNotMatches(songMatcher, "Author - Title Title")
    }

    @Test
    fun authorBeforeTitleMatch_lineWithEmptyAuthorOrTitleWhenEmptyAreAllowed_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-", """\w*""", """\w*""")

        assertMatches("Author", "", songMatcher, "Author -")
        assertMatches("", "Title", songMatcher, "- Title")
        assertMatches("", "", songMatcher, "-")
    }

    @Test
    fun authorBeforeTitleMatch_lineWithEmptyAuthorOrTitleWhenEmptyAreNotAllowed_notMatchedExpected() {
        val songMatcher = SongMatcher.newInstance(true, "-", """\w+""", """\w+""")

        assertNotMatches(songMatcher, "Author -")
        assertNotMatches(songMatcher, "- Title")
        assertNotMatches(songMatcher, "-")
    }

    @Test
    fun titleBeforeAuthorMatch_lineWithEmptyAuthorOrTitleWhenEmptyAreAllowed_matchedExpected() {
        val songMatcher = SongMatcher.newInstance(false, "-", """\w*""", """\w*""")

        assertMatches("Author", "", songMatcher, "- Author")
        assertMatches("", "Title", songMatcher, "Title -")
        assertMatches("", "", songMatcher, "-")
    }

    @Test
    fun titleBeforeAuthorMatch_lineWithEmptyAuthorOrTitleWhenEmptyAreNotAllowed_notMatchedExpected() {
        val songMatcher = SongMatcher.newInstance(false, "-", """\w+""", """\w+""")

        assertNotMatches(songMatcher, "- Author")
        assertNotMatches(songMatcher, "Title -")
        assertNotMatches(songMatcher, "-")
    }

    @Test
    fun matcherWithAllPossibleChars_lineWithPartOfSeparatorInsideOfTitleAndAuthor_matchedExpected() {
        val separator = "--"
        val songMatcher = SongMatcher.newInstanceWithAllPossibleChars(true, separator)

        assertMatches("Aut-hor", "Tit-le", songMatcher, "Aut-hor -- Tit-le")
    }

    @Test
    fun matcherWithAllPossibleChars_lineWithSeparatorInsideOfTitleAndAuthor_notMatchedExpected() {
        val separator = "-"
        val songMatcher = SongMatcher.newInstanceWithAllPossibleChars(true, separator)

        assertNotMatches(songMatcher, "Aut-hor - Tit-le")
    }

    @Test
    fun matcherWithAllPossibleChars_lineWithPartOfSeparatorInsideOfTitleAndAuthorWithSpacesInSeparator_notMatchedExpected() {
        val separator = " - "
        val songMatcher = SongMatcher.newInstanceWithAllPossibleChars(true, separator)

        assertMatches("Aut-hor", "Tit-le", songMatcher, "Aut-hor - Tit-le")
    }

    @Test
    fun matcherWithAllPossibleChars_lineWithSeparatorInsideOfTitleAndAuthorWithSpacesInSeparator_notMatchedExpected() {
        val separator = " - "
        val songMatcher = SongMatcher.newInstanceWithAllPossibleChars(true, separator)

        assertNotMatches(songMatcher, "Aut - hor - Tit - le")
    }

    private fun assertMatches(expectedAuthor: String, expectedTitle: String, matcher: SongMatcher, input: String) {
        val match = matcher.match(input)
        assertNotNull(match); match!!
        assertEquals(expectedAuthor, match.author)
        assertEquals(expectedTitle, match.title)
    }

    private fun assertNotMatches(matcher: SongMatcher, input: String) {
        assertNull(matcher.match(input))
    }
}