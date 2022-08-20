package ru.viafanasyev.guitarsongbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.layouts.SwipeRevealLayout

class LearnedSongsRecyclerAdapter(
    onSongClickListener: (song: Song, position: Int) -> Unit = { _, _ -> },
) : ListAdapter<Song, LearnedSongsRecyclerAdapter.LearnedSongViewHolder>(DIFF_CALLBACK) {

    private val actionListener: SwipeLayoutActionListener<Song> = SwipeLayoutActionListener(onSongClickListener)

    class LearnedSongViewHolder(itemView: SwipeRevealLayout) : RecyclerView.ViewHolder(itemView) {
        private val songItem: View = itemView.findViewById(R.id.songItem)
        private val songTitleTextView: TextView = songItem.findViewById(R.id.songTitle)
        private val songAuthorTextView: TextView = songItem.findViewById(R.id.songAuthor)
        private val button1: Button = itemView.findViewById(R.id.songButton1)
        private val button2: Button = itemView.findViewById(R.id.songButton2)

        fun bind(song: Song, position: Int, onClickListener: (song: Song, position: Int) -> Unit) {
            songTitleTextView.text = song.title
            songAuthorTextView.text = song.author
            songItem.setOnClickListener {
                onClickListener(song, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnedSongViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        check(itemView is SwipeRevealLayout)
        return LearnedSongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LearnedSongViewHolder, position: Int) {
        val itemView = holder.itemView
        check(itemView is SwipeRevealLayout)
        itemView.onOpen = actionListener::onItemOpen
        itemView.onClose = actionListener::onItemClose

        val song = getItem(position)
        holder.bind(song, position) { it, pos -> actionListener.onItemClick(itemView, it, pos) }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(actionListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(actionListener)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Song> = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song) = oldItem == newItem
        }
    }
}