package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActivityAddAndEditLearnedSongBinding
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras
import ru.viafanasyev.guitarsongbook.utils.validateInput

class AddAndEditLearnedSongActivity : AppCompatActivity() {

    private var _binding: ActivityAddAndEditLearnedSongBinding? = null

    private val binding get() = _binding!!

    private var editedSongId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddAndEditLearnedSongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val song = intent.getParcelableExtra<Song>(Extras.SONG)
        if (song != null) {
            // Song is edited
            editedSongId = song.id
            binding.learnedSongTitleEditText.setText(song.title)
            binding.learnedSongAuthorEditText.setText(song.author)
            title = getString(R.string.title_edit_learned_song)
        } else {
            // New song is added
            title = getString(R.string.title_add_learned_song)
        }

        binding.learnedSongTitleEditText.addTextChangedListener(afterTextChanged = {
            binding.learnedSongTitleInputLayout.error = null
        })
        binding.learnedSongAuthorEditText.addTextChangedListener(afterTextChanged = {
            binding.learnedSongAuthorInputLayout.error = null
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
            if (editedSongId == null) {
                // New song is created
                Song(songTitle, songAuthor, true)
            } else {
                // Song is edited
                Song(songTitle, songAuthor, true, editedSongId!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}