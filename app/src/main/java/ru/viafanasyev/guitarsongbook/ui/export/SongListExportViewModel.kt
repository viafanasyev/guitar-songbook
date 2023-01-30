package ru.viafanasyev.guitarsongbook.ui.export

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import ru.viafanasyev.guitarsongbook.domain.common.SongRepository
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem

class SongListExportViewModel(private val repository: SongRepository) : ViewModel() {
    val allLearned: LiveData<List<SongListItem>> = repository.allLearned.asLiveData()
    val allNotLearned: LiveData<List<SongListItem>> = repository.allNotLearned.asLiveData()

    class Factory(private val repository: SongRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SongListExportViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SongListExportViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}