package ru.viafanasyev.guitarsongbook.ui.notlearnedsongs

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
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

class NotLearnedSongsViewModel(private val repository: SongRepository) : ViewModel() {
    val allNotLearned: LiveData<List<Song>> = repository.allNotLearned.asLiveData()

    fun getById(songId: Int): LiveData<Song> = liveData {
        emit(
            withContext(Dispatchers.IO) {
                repository.getById(songId)
            }
        )
    }

    fun insertAll(vararg songs: Song) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAll(*songs)
    }

    fun update(song: Song) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(song)
    }

    fun delete(song: Song) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(song)
    }

    fun makeLearned(song: Song) = viewModelScope.launch(Dispatchers.IO) {
        require(!song.isLearned)
        repository.update(Song(song.title, song.author, true, song.id))
    }

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotLearnedSongsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotLearnedSongsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}