package ru.viafanasyev.guitarsongbook.ui.parsing

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import arrow.core.Either
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActivitySongListParsingBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.parser.ParseError
import ru.viafanasyev.guitarsongbook.parser.SongListParser
import ru.viafanasyev.guitarsongbook.parser.SongMatcher
import ru.viafanasyev.guitarsongbook.parser.parseAll
import ru.viafanasyev.guitarsongbook.utils.unzipEither

class SongListParsingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongListParsingBinding

    private val songListParsingViewModel: SongListParsingViewModel by viewModels {
        SongListParsingViewModel.Factory(DataAccessService.getInstance(this).songRepository)
    }

    private val separators: List<String> = listOf(
        " - ",
        " : ",
        " -- ",
        " | ",
    )
    private var selectedSeparatorPosition: Int = -1

    private val songFormatToParserCreator: List<Pair<Int, (String) -> SongListParser>> = listOf(
        Pair(
            R.string.drop_down_item_song_format_author_before_title,
            ::createAuthorBeforeTitleSongListParser
        ),
        Pair(
            R.string.drop_down_item_song_format_title_before_author,
            ::createTitleBeforeAuthorSongListParser
        ),
    )
    private val songFormats = mutableListOf<String>()
    private var selectedSongFormatPosition: Int = -1
    private lateinit var songFormatsAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListParsingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.songListEditText.addTextChangedListener(afterTextChanged = {
            binding.songListInputLayout.error = null
        })

        binding.songFormatsListDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedSongFormatPosition = position
            binding.songFormatsListInputLayout.error = null
        }

        binding.separatorsListDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedSeparatorPosition = position
            binding.separatorsListInputLayout.error = null

            val separator = separators.getOrNull(selectedSeparatorPosition)
            if (separator.isNullOrBlank()) {
                binding.separatorsListInputLayout.error = getString(R.string.error_song_separator_not_selected)
                return@setOnItemClickListener
            }

            onSeparatorSelected(separator)
        }

        songFormatsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            songFormats
        )
        binding.songFormatsListDropdown.setAdapter(songFormatsAdapter)

        binding.separatorsListDropdown.setAdapter(ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            separators.map { "'$it'" }
        ))

        binding.confirmButton.setOnClickListener {
            val text = binding.songListEditText.text?.toString()
            val separator = separators.getOrNull(selectedSeparatorPosition)
            val songParserCreator = songFormatToParserCreator.getOrNull(selectedSongFormatPosition)?.second
            if (text.isNullOrBlank()) {
                binding.songListInputLayout.error = getString(R.string.error_song_list_blank)
            } else if (separator.isNullOrBlank()) {
                binding.separatorsListInputLayout.error = getString(R.string.error_song_separator_not_selected)
            } else if (songParserCreator == null) {
                binding.songFormatsListInputLayout.error = getString(R.string.error_song_format_not_selected)
            } else {
                val parser = songParserCreator(separator)
                val cleanedLines = text.lines().filter { it.isNotBlank() }
                val parseResult = parser.parseAll(cleanedLines)
                showParseConfirmDialog(parseResult)
            }
        }
    }

    private fun showParseConfirmDialog(parseResult: List<Either<ParseError, Song>>) {
        parseResult.mapNotNull { it.swap().orNull() }
        val (notParsed, parsed) = parseResult.unzipEither()
        val notParsedString = notParsed.joinToString(
            separator = System.lineSeparator() + System.lineSeparator(),
            prefix = System.lineSeparator() + System.lineSeparator()
        ) {
            it.badLine
        }

        // TODO: Show not parsed elements better
        // TODO: Also show parsed songs
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_song_list_parsing_result)
            .setMessage(
                if (notParsed.isEmpty())
                    getString( // TODO: Падеж у числительных
                        R.string.dialog_message_song_list_parsing_result_all_parsed,
                        parsed.size
                    )
                else
                    getString( // TODO: Падеж у числительных
                        R.string.dialog_message_song_list_parsing_result_some_not_parsed,
                        parsed.size,
                        notParsed.size,
                        notParsedString
                    )
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                songListParsingViewModel.insertAll(parsed)
                this@SongListParsingActivity.finish()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun createAuthorBeforeTitleSongListParser(separator: String) =
        createSongListParser(true, separator)

    private fun createTitleBeforeAuthorSongListParser(separator: String) =
        createSongListParser(false, separator)

    private fun createSongListParser(authorBeforeTitle: Boolean, separator: String) =
        SongListParser(
            matcher = SongMatcher.newInstanceWithAllPossibleChars(
                authorBeforeTitle = authorBeforeTitle,
                separator = separator,
            ),
            asLearned = binding.asLearnedSwitch.isChecked,
        )

    private fun onSeparatorSelected(separator: String) {
        for (i in songFormatToParserCreator.indices) {
            val newFormat = getString(songFormatToParserCreator[i].first, separator)
            if (i <= songFormats.lastIndex) {
                songFormats[i] = newFormat
            } else {
                songFormats.add(newFormat)
            }
        }
        songFormatsAdapter.notifyDataSetChanged()
        if (selectedSongFormatPosition >= 0 && selectedSongFormatPosition <= songFormats.lastIndex) {
            binding.songFormatsListDropdown.setText(songFormats[selectedSongFormatPosition], false)
        }
    }
}