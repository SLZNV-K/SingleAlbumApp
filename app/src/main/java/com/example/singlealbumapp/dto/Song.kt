package com.example.singlealbumapp.dto

data class Song(
    val id: Long,
    val file: String,
    var isPlaying: Boolean = false,
    var liked: Boolean = false,
    var saved: Boolean = false,
)

data class SongState(
    val error: Boolean = false,
    val loading: Boolean = false,
)