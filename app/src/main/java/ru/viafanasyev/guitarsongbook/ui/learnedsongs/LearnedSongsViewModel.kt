package ru.viafanasyev.guitarsongbook.ui.learnedsongs

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
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song

class LearnedSongsViewModel(private val repository: SongRepository) : ViewModel() {
    val allLearned: LiveData<List<SongListItem>> = repository.allLearned.asLiveData()

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

    fun delete(songId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(songId)
    }

    fun moveToNotLearned(songId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.moveToNotLearned(songId)
    }

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LearnedSongsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LearnedSongsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}