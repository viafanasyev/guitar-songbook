package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActivityEditLearnedSongBinding
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras
import ru.viafanasyev.guitarsongbook.utils.validateInput
import kotlin.properties.Delegates

class EditLearnedSongActivity : AppCompatActivity() {

    private var _binding: ActivityEditLearnedSongBinding? = null

    private val binding get() = _binding!!

    private var songId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditLearnedSongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val song = intent.getParcelableExtra<Song>(Extras.SONG)
        if (song == null) {
            onInvalidInitialData()
            return
        }

        this.songId = song.id
        binding.learnedSongTitleEditText.setText(song.title)
        binding.learnedSongAuthorEditText.setText(song.author)

        binding.learnedSongTitleEditText.addTextChangedListener(afterTextChanged = {
            binding.learnedSongTitleInputLayout.error = null
        })
        binding.learnedSongAuthorEditText.addTextChangedListener(afterTextChanged = {
            binding.learnedSongAuthorInputLayout.error = null
        })

        binding.editLearnedSongButton.setOnClickListener { onEditButtonClick() }
    }

    private fun onInvalidInitialData() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun onEditButtonClick() {
        createSongOrNull()?.let { song ->
            val intent = Intent().putExtra(Extras.SONG, song)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun createSongOrNull(): Song? {
        val songTitle = validateInput(
            binding.learnedSongTitleEditText.text.toString(),
            String::isNotBlank,
            { binding.learnedSongTitleInputLayout.error = getString(R.string.error_song_title_blank) },
            { binding.learnedSongTitleInputLayout.error = null }
        )

        val songAuthor = validateInput(
            binding.learnedSongAuthorEditText.text.toString(),
            String::isNotBlank,
            { binding.learnedSongAuthorInputLayout.error = getString(R.string.error_song_author_blank) },
            { binding.learnedSongAuthorInputLayout.error = null }
        )

        return if (songTitle == null || songAuthor == null) {
            null
        } else {
            Song(songTitle, songAuthor, true, songId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}