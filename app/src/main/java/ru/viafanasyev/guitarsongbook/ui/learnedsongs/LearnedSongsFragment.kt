package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.adapter.LearnedSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentLearnedSongsBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.common.entities.Song
import ru.viafanasyev.guitarsongbook.domain.common.entities.SongListItem
import ru.viafanasyev.guitarsongbook.ui.actions.SongAction
import ru.viafanasyev.guitarsongbook.ui.actions.SongActionType
import ru.viafanasyev.guitarsongbook.ui.actions.SongActionsDialogFragment
import ru.viafanasyev.guitarsongbook.ui.detailed.SongActivity
import ru.viafanasyev.guitarsongbook.ui.edit.AddSongResultContract
import ru.viafanasyev.guitarsongbook.ui.edit.EditSongResultContract
import ru.viafanasyev.guitarsongbook.utils.Extras
import ru.viafanasyev.guitarsongbook.utils.FragmentTags
import ru.viafanasyev.guitarsongbook.utils.RequestKeys

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
            onSongAction = ::onSongAction,
            onSongDelete = ::onSongDelete,
        ).apply {
            learnedSongsViewModel.allLearned.observe(viewLifecycleOwner) { list ->
                submitList(list.sortedWith(compareBy({ it.author }, { it.title })))
            }
        }

        val addSongActivityLauncher =
            registerForActivityResult(AddSongResultContract(), this::onSongAdd)

        binding.fabAddLearnedSong.setOnClickListener {
            addSongActivityLauncher.launch(true)
        }

        learnedSongsViewModel.allLearned.observe(viewLifecycleOwner) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "${getString(R.string.title_learned_songs)} (${it.size})"
        }

        return binding.root
    }

    private fun onSongClick(song: SongListItem, position: Int) {
        val intent = Intent(activity, SongActivity::class.java)
        intent.putExtra(Extras.SONG_LIST_ITEM, song)
        startActivity(intent)
    }

    private fun onSongAction(song: SongListItem, position: Int) {
        // TODO: Move this call to `onCreateView`? How to get `song` and `position` there?
        childFragmentManager.setFragmentResultListener(RequestKeys.SONG_ACTION, viewLifecycleOwner) { _, bundle ->
            onSongActionDialogResult(song, position, bundle)
            childFragmentManager.clearFragmentResultListener(RequestKeys.SONG_ACTION)
        }

        SongActionsDialogFragment.newInstance(
            SongAction(SongActionType.SONG_EDIT, R.layout.action_song_edit),
            SongAction(SongActionType.MOVE_SONG_TO_NOT_LEARNED, R.layout.action_move_song_to_not_learned),
        ).show(childFragmentManager, FragmentTags.SONG_ACTION_DIALOG)
    }

    private fun onSongActionDialogResult(song: SongListItem, position: Int, bundle: Bundle) {
        when (val songAction = bundle.getSerializable(Extras.SONG_ACTION) as SongActionType?) {
            SongActionType.SONG_EDIT -> onSongEditRequest(song, position)
            SongActionType.MOVE_SONG_TO_NOT_LEARNED -> onSongMoveToNotLearned(song, position)
            null -> { /* Dialog is cancelled -> do nothing */ }
            else -> throw UnsupportedOperationException("Unexpected song action $songAction for learned song $song")
        }
    }

    private fun onSongMoveToNotLearned(song: SongListItem, position: Int) {
        require(song.isLearned)
        learnedSongsViewModel.moveToNotLearned(song.id).invokeOnCompletion {
            Snackbar.make(
                binding.root,
                getString(R.string.message_moved_song_to_not_learned, song.author, song.title),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onSongEditRequest(song: SongListItem, position: Int) {
        editSongActivityLauncher.launch(song)
    }

    private fun onSongEdit(song: Song?) {
        if (song != null) {
            learnedSongsViewModel.update(song)
        }
    }

    private fun onSongDelete(song: SongListItem, position: Int) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_title_delete_song)
            .setMessage(getString(R.string.dialog_message_delete_song, song.author, song.title))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                learnedSongsViewModel.delete(song.id)
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