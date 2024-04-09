package com.example.singlealbumapp.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.singlealbumapp.adapter.OnInteractionListener
import com.example.singlealbumapp.adapter.SongAdapter
import com.example.singlealbumapp.databinding.ActivityMainBinding
import com.example.singlealbumapp.dto.Song
import com.example.singlealbumapp.viewmodel.AlbumViewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val albumViewModel: AlbumViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = SongAdapter(object : OnInteractionListener {

            override fun onPlay(song: Song) {
                albumViewModel.playSong(song.file)
            }

            override fun onSave(song: Song) {
                albumViewModel.saveSong(song)
            }
        })

        binding.apply {
            recycler.adapter = adapter
            albumViewModel.dataState.observe(this@MainActivity) { state ->
                noSongs.isVisible = state.error
            }
            albumViewModel.data.observe(this@MainActivity) { album ->
                adapter.submitList(album.tracks)
                albumTitle.text = album.title
                performerName.text = album.artist
                genre.text = album.genre
                yearRelease.text = album.published
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mediaPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
        }
    }

    private fun initializeSeekBar() {
        mediaPlayer = albumViewModel.observer.mediaPlayer
        binding.apply {
            println("ViewModel: $mediaPlayer")
            println("Time:" + convertToMMSS(mediaPlayer?.duration.toString()))

            seekBar.max = mediaPlayer!!.duration
            duration.text = convertToMMSS(mediaPlayer?.duration.toString())
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        seekBar.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        seekBar.progress = 0
                    }
                }
            }, 0)
        }
    }

    private fun convertToMMSS(duration: String): String {
        val millis = duration.toLong()
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }


}