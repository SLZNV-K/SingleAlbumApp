package com.example.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.singlealbumapp.R
import com.example.singlealbumapp.databinding.CardSongBinding
import com.example.singlealbumapp.dto.Song

interface OnInteractionListener {
    fun onPlay(song: Song)
    fun onSave(song: Song)
}


class SongAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Song, SongViewHolder>(SongDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }
}

class SongViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song) {
        with(binding) {
            songName.text = song.file

            download.isVisible = song.saved

            playPauseButton.setImageResource(
                if (song.isPlaying) {
                    R.drawable.pause
                } else {
                    R.drawable.play
                }
            )
            playPauseButton.setOnClickListener {
                onInteractionListener.onPlay(song)
            }

            likeButton.setOnClickListener {
                val isSelected = song.liked
                it.isSelected = !isSelected
                song.liked = !isSelected

            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.song_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {

                            R.id.save -> {
//                                onInteractionListener.onSave(song)
                                song.saved = !song.saved
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object SongDiffCallBack : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
}