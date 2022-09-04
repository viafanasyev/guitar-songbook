package ru.viafanasyev.guitarsongbook.ui.notlearnedsongs

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.adapter.NotLearnedSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentNotLearnedSongsBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.ui.detailed.SongActivity
import ru.viafanasyev.guitarsongbook.ui.edit.AddSongResultContract
import ru.viafanasyev.guitarsongbook.ui.edit.EditSongResultContract
import ru.viafanasyev.guitarsongbook.utils.Extras

class NotLearnedSongsFragment : Fragment() {

    private var _binding: FragmentNotLearnedSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val notLearnedSongsViewModel: NotLearnedSongsViewModel by viewModels {
        NotLearnedSongsViewModel.Factory(DataAccessService.getInstance(requireContext()).songRepository)
    }

    private val editSongActivityLauncher =
        registerForActivityResult(EditSongResultContract(), this::onSongEdit)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotLearnedSongsBinding.inflate(inflater, container, false)

        val notLearnedSongsRecyclerView = binding.notLearnedSongsRecyclerView
        notLearnedSongsRecyclerView.layoutManager = LinearLayoutManager(context)
        notLearnedSongsRecyclerView.adapter  = NotLearnedSongsRecyclerAdapter(
            onSongClickListener = ::onSongClick,
            onSongMoveToLearned = ::onSongMoveToLearned,
            onSongEdit = ::onSongEditRequest,
            onSongDelete = ::onSongDelete,
        ).apply {
            notLearnedSongsViewModel.allNotLearned.observe(viewLifecycleOwner, ::submitList)
        }

        val addSongActivityLauncher =
            registerForActivityResult(AddSongResultContract(), this::onSongAdd)

        binding.fabAddNotLearnedSong.setOnClickListener {
            addSongActivityLauncher.launch(false)
        }

        return binding.root
    }

    private fun onSongClick(song: Song, position: Int) {
        val intent = Intent(activity, SongActivity::class.java)
        intent.putExtra(Extras.SONG, song)
        startActivity(intent)
    }

    private fun onSongMoveToLearned(song: Song, position: Int) {
        notLearnedSongsViewModel.moveToLearned(song).invokeOnCompletion {
            Snackbar.make(
                binding.root,
                getString(R.string.message_moved_song_to_learned, song.author, song.title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onSongEditRequest(song: Song, position: Int) {
        editSongActivityLauncher.launch(song)
    }

    private fun onSongEdit(song: Song?) {
        if (song != null) {
            notLearnedSongsViewModel.update(song)
        }
    }

    private fun onSongDelete(song: Song, position: Int) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title_delete_song)
            .setMessage(getString(R.string.dialog_message_delete_song, song.author, song.title))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                notLearnedSongsViewModel.delete(song)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun onSongAdd(song: Song?) {
        if (song == null) {
            return
        }

        notLearnedSongsViewModel.insertAll(song).invokeOnCompletion {
            Snackbar.make(
                binding.root,
                getString(R.string.message_added_song, song.author, song.title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}