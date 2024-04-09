package com.example.singlealbumapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.singlealbumapp.MediaLifecycleObserver
import com.example.singlealbumapp.dto.Album
import com.example.singlealbumapp.dto.Song
import com.example.singlealbumapp.dto.SongState
import com.example.singlealbumapp.repository.SongRepositoryImpl
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    private val repository = SongRepositoryImpl()

    val observer = MediaLifecycleObserver()

    private val _data = MutableLiveData<Album>()
    val data: LiveData<Album>
        get() = _data

    private val _dataState = MutableLiveData<SongState>()
    val dataState: LiveData<SongState>
        get() = _dataState

    init {
        getSongs()
        observer.mediaPlayer?.setOnCompletionListener {
            val songs = data.value?.tracks ?: return@setOnCompletionListener
            val nextIndex = songs.indexOfFirst { it.isPlaying }.plus(1)
            val currentSongIndex = if (nextIndex > songs.size) {
                0
            } else {
                nextIndex
            }
            playSong(songs[currentSongIndex].file)
            println("ViewModel: ${observer.mediaPlayer}")
        }
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

    fun playSong(file: String) {
        _data.postValue(_data.value!!.copy(tracks = data.value!!.tracks.map { song ->
            if (song.file == file) {
                val playing = song.isPlaying
                observer.apply {
                    if (playing) {
                        pause()
                    } else {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource("${SongRepositoryImpl.BASE_URL}$file")
                        play()
                    }
                }
                song.copy(isPlaying = !song.isPlaying)
            } else {
                song.copy(isPlaying = false)
            }
        }))
    }

    fun saveSong(song: Song) {
        _data.postValue(_data.value!!.copy(tracks = data.value!!.tracks.map {
            if (it == song) {
                it.copy(saved = !song.saved)
            } else {
                it
            }
        }))
    }
}

