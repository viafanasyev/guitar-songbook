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
    onSongMakeLearned: (song: Song, position: Int) -> Unit = { _, _ -> },
    onSongEdit: (song: Song, position: Int) -> Unit = { _, _ -> },
    onSongDelete: (song: Song, position: Int) -> Unit = { _, _ -> },
) : ListAdapter<Song, NotLearnedSongsRecyclerAdapter.NotLearnedSongViewHolder>(DIFF_CALLBACK) {

    class NotLearnedSongsActionListener(
        onSongClickListener: (song: Song, position: Int) -> Unit = { _, _ -> },
        onSongMakeLearned: (song: Song, position: Int) -> Unit = { _, _ -> },
        onSongEdit: (song: Song, position: Int) -> Unit = { _, _ -> },
        onSongDelete: (song: Song, position: Int) -> Unit = { _, _, -> },
    ) : SwipeLayoutActionListener<Song, NotLearnedSongsActionListener.ActionType>(
        onItemClick = onSongClickListener,
        onItemButtonClick = { item, position, actionType ->
            when (actionType) {
                ActionType.MAKE_LEARNED -> onSongMakeLearned(item, position)
                ActionType.EDIT -> onSongEdit(item, position)
                ActionType.DELETE -> onSongDelete(item, position)
                else -> throw UnsupportedOperationException("Unknown action type $actionType")
            }
        }
    ) {
        enum class ActionType {
            MAKE_LEARNED,
            EDIT,
            DELETE,
        }
    }

    private val actionListener = NotLearnedSongsActionListener(
        onSongClickListener = onSongClickListener,
        onSongMakeLearned = onSongMakeLearned,
        onSongEdit = onSongEdit,
        onSongDelete = onSongDelete,
    )

    class NotLearnedSongViewHolder(private val binding: NotLearnedSongItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val root: SwipeRevealLayout = binding.root
        private val songItem: View = binding.songItem
        private val songTitleTextView: TextView = binding.songTitle
        private val songAuthorTextView: TextView = binding.songAuthor
        private val buttonMakeLearned: ImageButton = binding.buttonMoveSongToLearned.root
        private val buttonEdit: ImageButton = binding.buttonEditSong.root
        private val buttonDelete: ImageButton = binding.buttonDeleteSong.root

        fun bind(song: Song, position: Int, actionListener: NotLearnedSongsActionListener) {
            root.onOpen = { actionListener.onItemOpen(root) }
            root.onClose = { actionListener.onItemClose(root) }
            songTitleTextView.text = song.title
            songAuthorTextView.text = song.author
            songItem.setOnClickListener {
                actionListener.onItemClick(root, song, position)
            }
            buttonMakeLearned.setOnClickListener {
                actionListener.onItemButtonClick(root, song, position, NotLearnedSongsActionListener.ActionType.MAKE_LEARNED)
            }
            buttonEdit.setOnClickListener {
                actionListener.onItemButtonClick(root, song, position, NotLearnedSongsActionListener.ActionType.EDIT)
            }
            buttonDelete.setOnClickListener {
                actionListener.onItemButtonClick(root, song, position, NotLearnedSongsActionListener.ActionType.DELETE)
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