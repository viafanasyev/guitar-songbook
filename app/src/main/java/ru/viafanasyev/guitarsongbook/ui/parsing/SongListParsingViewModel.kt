package ru.viafanasyev.guitarsongbook.ui.parsing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

class SongListParsingViewModel(private val repository: SongRepository) : ViewModel() {

    fun insertAll(songs: Collection<Song>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAll(songs)
    }

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SongListParsingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SongListParsingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}