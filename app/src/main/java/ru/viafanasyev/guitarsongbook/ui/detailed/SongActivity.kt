package ru.viafanasyev.guitarsongbook.ui.detailed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.utils.Extras

class SongActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)

        val song = intent.getParcelableExtra<SongListItem>(Extras.SONG_LIST_ITEM)!!
        title = song.title
        supportActionBar?.let { it.subtitle = song.author }
    }
}