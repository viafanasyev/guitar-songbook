package ru.viafanasyev.guitarsongbook.ui.knownsongs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.viafanasyev.guitarsongbook.domain.Song

class KnownSongsViewModel : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>().apply {
        value = (0..30).map { Song("Песня $it", "Автор $it") }
    }
    val songs: LiveData<List<Song>> = _songs
}