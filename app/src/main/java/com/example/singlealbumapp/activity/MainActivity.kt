package com.example.singlealbumapp.activity

import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.singlealbumapp.MediaLifecycleObserver
import com.example.singlealbumapp.adapter.OnInteractionListener
import com.example.singlealbumapp.adapter.SongAdapter
import com.example.singlealbumapp.databinding.ActivityMainBinding
import com.example.singlealbumapp.dto.Song
import com.example.singlealbumapp.viewmodel.AlbumViewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val albumViewModel: AlbumViewModel by viewModels()
    private val observer = MediaLifecycleObserver()
    private var currentSongIndex: Int = 0
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = SongAdapter(object : OnInteractionListener {

            override fun onPlay(song: Song) {
                playTrack(song)
            }

            override fun onSave(song: Song) {
                TODO()
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

            observer.mediaPlayer?.setOnCompletionListener {
                val songs = albumViewModel.data.value?.tracks ?: return@setOnCompletionListener
                if (currentSongIndex == songs.size - 1) currentSongIndex = 0

                if (currentSongIndex < songs.size - 1) {
                    currentSongIndex++
                    playTrack(songs[currentSongIndex])
                }
                initializeSeekBar()
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekbar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) observer.mediaPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
        }
    }

    private fun initializeSeekBar() {
        binding.apply {
            seekBar.max = observer.mediaPlayer!!.duration
            duration.text = convertToMMSS(observer.mediaPlayer?.duration.toString())
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        seekBar.progress = observer.mediaPlayer!!.currentPosition
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        seekBar.progress = 0
                    }
                }
            }, 0)
        }
    }

    fun playTrack(song: Song) {
        val songUrl =
            "${BASE_URL}${song.file}"
        observer.apply {
            if (mediaPlayer?.isPlaying == true) {
                pause()
                mediaPlayer?.reset()
                if (song.isPlaying) {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(songUrl)
                    play()
                }
            } else {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(songUrl)
                play()

            }
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

    companion object {
        const val BASE_URL =
            "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    }

}