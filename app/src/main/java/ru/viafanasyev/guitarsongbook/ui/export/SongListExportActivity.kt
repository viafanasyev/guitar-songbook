package ru.viafanasyev.guitarsongbook.ui.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActivitySongListExportBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService

class SongListExportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongListExportBinding

    private val songListParsingViewModel: SongListExportViewModel by viewModels {
        SongListExportViewModel.Factory(DataAccessService.getInstance(this).songRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songListParsingViewModel.allLearned.observe(this) { allLearned ->
            binding.exportedLearnedSongList.text = allLearned.joinToString(System.lineSeparator()) { it.author + " - " + it.title }
        }
        songListParsingViewModel.allNotLearned.observe(this) { allNotLearned ->
            binding.exportedNotLearnedSongList.text = allNotLearned.joinToString(System.lineSeparator()) { it.author + " - " + it.title }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.song_list_export_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.copyToClipboard -> {
                copyAllSongsToClipboard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun copyAllSongsToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(
            getString(R.string.clipboard_label_exported_song_list),
            getAllText()
        )
        clipboard.setPrimaryClip(clip)

        Snackbar.make(
            binding.root,
            getString(R.string.message_songs_copied_to_clipboard),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getAllText(): String {
        return binding.exportedLearnedSongListTitle.text.toString() +
            System.lineSeparator() + System.lineSeparator() +
            binding.exportedLearnedSongList.text.toString() +
            System.lineSeparator() + System.lineSeparator() +
            binding.exportedNotLearnedSongListTitle.text.toString() +
            System.lineSeparator() + System.lineSeparator() +
            binding.exportedNotLearnedSongList.text.toString()
    }
}