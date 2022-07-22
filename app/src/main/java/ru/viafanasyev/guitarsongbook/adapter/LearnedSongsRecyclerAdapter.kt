package ru.viafanasyev.guitarsongbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.domain.entities.Song

class LearnedSongsRecyclerAdapter(
    private val onSongClickListener: (song: Song, position: Int) -> Unit = { _, _ -> },
) : ListAdapter<Song, LearnedSongsRecyclerAdapter.LearnedSongViewHolder>(DIFF_CALLBACK) {

    class LearnedSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songTitleTextView: TextView = itemView.findViewById(R.id.songTitle)
        private val songAuthorTextView: TextView = itemView.findViewById(R.id.songAuthor)

        fun bind(song: Song) {
            songTitleTextView.text = song.title
            songAuthorTextView.text = song.author
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnedSongViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return LearnedSongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LearnedSongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
        holder.itemView.setOnClickListener {
            onSongClickListener(song, position)
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Song> = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
        }
    }
}