package ru.viafanasyev.guitarsongbook.ui.learnedsongs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.viafanasyev.guitarsongbook.adapter.LearnedSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentLearnedSongsBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class LearnedSongsFragment : Fragment() {

    private var _binding: FragmentLearnedSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val learnedSongsViewModel: LearnedSongsViewModel by viewModels {
        LearnedSongsViewModel.Factory(DataAccessService.getInstance(requireContext()).songRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnedSongsBinding.inflate(inflater, container, false)

        val learnedSongsRecyclerView = binding.learnedSongsRecyclerView
        learnedSongsRecyclerView.layoutManager = LinearLayoutManager(context)
        learnedSongsViewModel.allLearned.observe(viewLifecycleOwner) {
            learnedSongsRecyclerView.adapter = LearnedSongsRecyclerAdapter(it, ::onSongClick)
        }
        return binding.root
    }

    private fun onSongClick(song: Song, position: Int) {
        val intent = Intent(activity, LearnedSongActivity::class.java)
        intent.putExtra(Extras.SONG, song)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}