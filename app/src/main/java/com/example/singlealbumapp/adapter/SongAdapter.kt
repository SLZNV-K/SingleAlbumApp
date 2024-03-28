package com.example.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.singlealbumapp.databinding.CardSongBinding
import com.example.singlealbumapp.dto.Song

class SongAdapter : ListAdapter<Song, SongViewHolder>(SongDiffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position) ?: return
        holder.bind(song)
    }
}

class SongViewHolder(private val binding: CardSongBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song) {
        with(binding) {
            songName.text = song.songName
            performer.text = song.performer
        }
    }
}

object SongDiffCallBack : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
}