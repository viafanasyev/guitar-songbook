package ru.viafanasyev.guitarsongbook.ui.knownsongs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.domain.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class KnownSongActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_known_song)

        val song = intent.getParcelableExtra<Song>(Extras.SONG)!!
        title = song.title
        supportActionBar?.let { it.subtitle = song.author }
    }
}