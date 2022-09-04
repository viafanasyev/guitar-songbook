package ru.viafanasyev.guitarsongbook.ui.learnedsongs

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
import ru.viafanasyev.guitarsongbook.adapter.LearnedSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentLearnedSongsBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.ui.detailed.SongActivity
import ru.viafanasyev.guitarsongbook.ui.edit.AddSongResultContract
import ru.viafanasyev.guitarsongbook.ui.edit.EditSongResultContract
import ru.viafanasyev.guitarsongbook.utils.Extras

class LearnedSongsFragment : Fragment() {

    private var _binding: FragmentLearnedSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val learnedSongsViewModel: LearnedSongsViewModel by viewModels {
        LearnedSongsViewModel.Factory(DataAccessService.getInstance(requireContext()).songRepository)
    }

    private val editSongActivityLauncher =
        registerForActivityResult(EditSongResultContract(), this::onSongEdit)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnedSongsBinding.inflate(inflater, container, false)

        val learnedSongsRecyclerView = binding.learnedSongsRecyclerView
        learnedSongsRecyclerView.layoutManager = LinearLayoutManager(context)
        learnedSongsRecyclerView.adapter  = LearnedSongsRecyclerAdapter(
            onSongClickListener = ::onSongClick,
            onSongMoveToNotLearned = ::onSongMoveToNotLearned,
            onSongEdit = ::onSongEditRequest,
            onSongDelete = ::onSongDelete,
        ).apply {
            learnedSongsViewModel.allLearned.observe(viewLifecycleOwner, ::submitList)
        }

        val addSongActivityLauncher =
            registerForActivityResult(AddSongResultContract(), this::onSongAdd)

        binding.fabAddLearnedSong.setOnClickListener {
            addSongActivityLauncher.launch(true)
        }

        return binding.root
    }

    private fun onSongClick(song: Song, position: Int) {
        val intent = Intent(activity, SongActivity::class.java)
        intent.putExtra(Extras.SONG, song)
        startActivity(intent)
    }

    private fun onSongMoveToNotLearned(song: Song, position: Int) {
        learnedSongsViewModel.moveToNotLearned(song).invokeOnCompletion {
            Snackbar.make(
                binding.root,
                getString(R.string.message_moved_song_to_not_learned, song.author, song.title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onSongEditRequest(song: Song, position: Int) {
        editSongActivityLauncher.launch(song)
    }

    private fun onSongEdit(song: Song?) {
        if (song != null) {
            learnedSongsViewModel.update(song)
        }
    }

    private fun onSongDelete(song: Song, position: Int) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title_delete_song)
            .setMessage(getString(R.string.dialog_message_delete_song, song.author, song.title))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                learnedSongsViewModel.delete(song)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun onSongAdd(song: Song?) {
        if (song == null) {
            return
        }

        learnedSongsViewModel.insertAll(song).invokeOnCompletion {
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