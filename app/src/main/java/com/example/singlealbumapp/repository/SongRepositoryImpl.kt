package com.example.singlealbumapp.repository

import com.example.singlealbumapp.dto.Album
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class SongRepositoryImpl {
    private val client = OkHttpClient()

    fun getAlbum(callback: (Album) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL}album.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw IOException("Album data body is null")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val album = Gson().fromJson(body, Album::class.java)
                callback(album)
            }
        })
    }

    companion object {
        const val BASE_URL =
            "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    }
}