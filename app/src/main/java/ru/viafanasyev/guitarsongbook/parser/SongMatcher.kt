package ru.viafanasyev.guitarsongbook.parser

class SongMatcher private constructor(
    private val regex: Regex,
) {
    data class Match(
        val title: String,
        val author: String,
    )

    fun match(line: String): Match? {
        val groups = regex.matchEntire(line)?.groups as? MatchNamedGroupCollection ?: return null
        val title = groups["title"]?.value ?: return null
        val author = groups["author"]?.value ?: return null
        return Match(title, author)
    }

    companion object {
        private const val ALL_PUNCTUATION = "\\'!\"#$%&()*+,-./:;<=>?@[]^_`{|}~"

        /**
         * Returns new instance of [SongMatcher],
         * where title and author patterns allow all characters, except [separator].
         */
        fun newInstanceWithAllPossibleChars(
            authorBeforeTitle: Boolean,
            separator: String,
        ) = newInstance(
            authorBeforeTitle = authorBeforeTitle,
            separator = separator,
            titlePattern = anyCharExceptSeparatorRegex(separator) + "+?", // Non-greedy, so leading and trailing spaces won't be captured
            authorPattern = anyCharExceptSeparatorRegex(separator) + "+?",
        )

        private fun anyCharExceptSeparatorRegex(separator: String): String {
            return """((\w|\d|\s|[${Regex.escape(ALL_PUNCTUATION)}])(?<!${Regex.escape(separator)}))"""
        }

        fun newInstance(
            authorBeforeTitle: Boolean,
            separator: String,
            titlePattern: String = """\w+""",
            authorPattern: String = """\w+""",
        ): SongMatcher {
            val prefix = """(\s*)"""
            val infix = """(\s*)${Regex.escape(separator)}(\s*)"""
            val suffix = """(\s*)"""
            val authorGroup = """(?<author>$authorPattern)"""
            val titleGroup = """(?<title>$titlePattern)"""
            return SongMatcher(if (authorBeforeTitle) {
                "$prefix$authorGroup$infix$titleGroup$suffix".toRegex()
            } else {
                "$prefix$titleGroup$infix$authorGroup$suffix".toRegex()
            })
        }
    }
}