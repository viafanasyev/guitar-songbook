package ru.viafanasyev.guitarsongbook.ui.knownsongs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.entities.Song

class KnownSongsViewModel(private val repository: SongRepository) : ViewModel() {
    val allKnown: LiveData<List<Song>> = repository.allKnown.asLiveData()

    fun getById(songId: Int): LiveData<Song> = liveData {
        emit(
            withContext(Dispatchers.IO) {
                repository.getById(songId)
            }
        )
    }

    fun insertAll(vararg songs: Song) = viewModelScope.launch {
        repository.insertAll(*songs)
    }

    fun delete(user: Song) = viewModelScope.launch {
        repository.delete(user)
    }

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(KnownSongsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return KnownSongsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}