package ru.viafanasyev.guitarsongbook.ui.knownsongs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.viafanasyev.guitarsongbook.adapter.KnownSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentKnownSongsBinding
import ru.viafanasyev.guitarsongbook.domain.DataAccessService
import ru.viafanasyev.guitarsongbook.domain.entities.Song
import ru.viafanasyev.guitarsongbook.utils.Extras

class KnownSongsFragment : Fragment() {

    private var _binding: FragmentKnownSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val knownSongsViewModel: KnownSongsViewModel by viewModels {
        KnownSongsViewModel.Factory(DataAccessService.getInstance(requireContext()).songRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKnownSongsBinding.inflate(inflater, container, false)

        val knownSongsRecyclerView = binding.knownSongsRecyclerView
        knownSongsRecyclerView.layoutManager = LinearLayoutManager(context)
        knownSongsViewModel.allKnown.observe(viewLifecycleOwner) {
            knownSongsRecyclerView.adapter = KnownSongsRecyclerAdapter(it, ::onSongClick)
        }
        return binding.root
    }

    private fun onSongClick(song: Song, position: Int) {
        val intent = Intent(activity, KnownSongActivity::class.java)
        intent.putExtra(Extras.SONG, song)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}