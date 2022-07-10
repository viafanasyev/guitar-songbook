package ru.viafanasyev.guitarsongbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.domain.entities.Song

class KnownSongsRecyclerAdapter(
    private val songs: List<Song>,
    private val onSongClickListener: (song: Song, position: Int) -> Unit = { _, _ -> },
) : RecyclerView.Adapter<KnownSongsRecyclerAdapter.KnownSongViewHolder>() {

    class KnownSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songTitleTextView: TextView = itemView.findViewById(R.id.songTitle)
        private val songAuthorTextView: TextView = itemView.findViewById(R.id.songAuthor)

        fun bind(song: Song) {
            songTitleTextView.text = song.title
            songAuthorTextView.text = song.author
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnownSongViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return KnownSongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KnownSongViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.itemView.setOnClickListener {
            onSongClickListener(songs[position], position)
        }
    }

    override fun getItemCount() = songs.size
}