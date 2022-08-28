package ru.viafanasyev.guitarsongbook.ui.edit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActivityAddAndEditSongBinding
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras
import ru.viafanasyev.guitarsongbook.utils.validateInput
import kotlin.properties.Delegates

class AddAndEditSongActivity : AppCompatActivity() {

    private var _binding: ActivityAddAndEditSongBinding? = null

    private val binding get() = _binding!!

    private var editedSongId: Int? = null

    private var isLearned: Boolean by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddAndEditSongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<Song>(Extras.SONG)?.let { song ->
            // Song is edited
            editedSongId = song.id
            binding.songTitleEditText.setText(song.title)
            binding.songAuthorEditText.setText(song.author)
        }

        if (!intent.hasExtra(Extras.IS_LEARNED)) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        isLearned = intent.getBooleanExtra(Extras.IS_LEARNED, false)

        binding.songTitleEditText.addTextChangedListener(afterTextChanged = {
            binding.songTitleInputLayout.error = null
        })
        binding.songAuthorEditText.addTextChangedListener(afterTextChanged = {
            binding.songAuthorInputLayout.error = null
        })

        binding.confirmButton.setOnClickListener { onConfirmButtonClick() }
    }

    private fun onConfirmButtonClick() {
        createSongOrNull()?.let { song ->
            val intent = Intent().putExtra(Extras.SONG, song)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun createSongOrNull(): Song? {
        val songTitle = validateInput(
            binding.songTitleEditText.text.toString(),
            String::isNotBlank,
            { binding.songTitleInputLayout.error = getString(R.string.error_song_title_blank) },
            { binding.songTitleInputLayout.error = null }
        )

        val songAuthor = validateInput(
            binding.songAuthorEditText.text.toString(),
            String::isNotBlank,
            { binding.songAuthorInputLayout.error = getString(R.string.error_song_author_blank) },
            { binding.songAuthorInputLayout.error = null }
        )

        return if (songTitle == null || songAuthor == null) {
            null
        } else {
            if (editedSongId == null) {
                // New song is created
                Song(songTitle, songAuthor, isLearned)
            } else {
                // Song is edited
                Song(songTitle, songAuthor, isLearned, editedSongId!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}