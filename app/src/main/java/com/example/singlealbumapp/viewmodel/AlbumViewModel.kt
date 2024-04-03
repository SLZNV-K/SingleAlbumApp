package com.example.singlealbumapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlealbumapp.dto.Album
import com.example.singlealbumapp.dto.SongState
import com.example.singlealbumapp.repository.SongRepositoryImpl
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    private val repository = SongRepositoryImpl()
    private val _data = MutableLiveData<Album>()
    val data: LiveData<Album>
        get() = _data

    private val _dataState = MutableLiveData<SongState>()
    val dataState: LiveData<SongState>
        get() = _dataState

    init {
        getSongs()
    }

    private fun getSongs() {
        viewModelScope.launch {
            _dataState.value = SongState(loading = true)
            try {
                repository.getAlbum { album ->
                    _data.postValue(album)
                }
                _dataState.value = SongState()
            } catch (e: Exception) {
                _dataState.value = SongState(error = true)
            }
        }
    }
}

