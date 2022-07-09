package ru.viafanasyev.guitarsongbook.ui.knownsongs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.viafanasyev.guitarsongbook.adapter.KnownSongsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentKnownSongsBinding
import ru.viafanasyev.guitarsongbook.domain.Song

class KnownSongsFragment : Fragment() {

    private var _binding: FragmentKnownSongsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKnownSongsBinding.inflate(inflater, container, false)

        val knownSongsViewModel = ViewModelProvider(this).get(KnownSongsViewModel::class.java)

        val knownSongsRecyclerView = binding.knownSongsRecyclerView
        knownSongsRecyclerView.layoutManager = LinearLayoutManager(context)
        knownSongsViewModel.songs.observe(viewLifecycleOwner) {
            knownSongsRecyclerView.adapter = KnownSongsRecyclerAdapter(it, ::onSongClick)
        }
        return binding.root
    }

    private fun onSongClick(song: Song, position: Int) {
        Toast.makeText(
            context,
            "Clicked song ${song.songName} at position $position",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}