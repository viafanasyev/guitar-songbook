package ru.viafanasyev.guitarsongbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.databinding.NotLearnedSongItemBinding
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.layouts.SwipeRevealLayout

class NotLearnedSongsRecyclerAdapter(
    onSongClickListener: (song: Song, position: Int) -> Unit = { _, _ -> },
    onSongAction: (song: Song, position: Int) -> Unit = { _, _ -> },
    onSongDelete: (song: Song, position: Int) -> Unit = { _, _ -> },
) : ListAdapter<Song, NotLearnedSongsRecyclerAdapter.NotLearnedSongViewHolder>(DIFF_CALLBACK) {

    private val actionListener = SwipeLayoutActionListener(
        onItemClick = onSongClickListener,
        onItemAction = onSongAction,
        onItemDelete = onSongDelete,
    )

    class NotLearnedSongViewHolder(private val binding: NotLearnedSongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val root: SwipeRevealLayout = binding.root
        private val songItem: View = binding.songItem
        private val songTitleTextView: TextView = binding.songTitle
        private val songAuthorTextView: TextView = binding.songAuthor
        private val buttonAction: ImageButton = binding.buttonSongAction.root
        private val buttonDelete: ImageButton = binding.buttonDeleteSong.root

        fun bind(song: Song, position: Int, actionListener: SwipeLayoutActionListener<Song>) {
            root.onOpen = { actionListener.onItemOpen(root) }
            root.onClose = { actionListener.onItemClose(root) }
            songTitleTextView.text = song.title
            songAuthorTextView.text = song.author
            songItem.setOnClickListener {
                actionListener.onItemClick(root, song, position)
            }
            buttonAction.setOnClickListener {
                actionListener.onItemAction(root, song, position)
            }
            buttonDelete.setOnClickListener {
                actionListener.onItemDelete(root, song, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotLearnedSongViewHolder {
        val binding = NotLearnedSongItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotLearnedSongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotLearnedSongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song, position, actionListener)
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