package ru.viafanasyev.guitarsongbook.ui.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.viafanasyev.guitarsongbook.adapter.SongActionsRecyclerAdapter
import ru.viafanasyev.guitarsongbook.databinding.FragmentSongActionsDialogBinding
import ru.viafanasyev.guitarsongbook.utils.Extras
import ru.viafanasyev.guitarsongbook.utils.RequestKeys

class SongActionsDialogFragment private constructor() : BottomSheetDialogFragment() {

    private var _binding: FragmentSongActionsDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongActionsDialogBinding.inflate(inflater, container, false)

        val actions = arguments?.getParcelableArrayList<SongAction>(Extras.SONG_ACTIONS)
        require(!actions.isNullOrEmpty()) { "SongActionsDialogFragment must be provided with possible actions" }
        assert(actions.distinctBy { it.actionType }.size == actions.size) { "Song Actions should be unique" }

        val recyclerView = binding.actions
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SongActionsRecyclerAdapter(actions) { actionType ->
            setFragmentResult(RequestKeys.SONG_ACTION, bundleOf(Extras.SONG_ACTION to actionType))
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // TODO: Make tests for this function?
        //  1. Check that bad arguments ar not accepted
        //  2. Check that only valid Fragments (i.e. with non-empty actions argument) are returned
        fun newInstance(vararg actions: SongAction): SongActionsDialogFragment {
            require(actions.isNotEmpty()) { "SongActionsDialogFragment must be provided with possible actions" }
            assert(actions.distinctBy { it.actionType }.size == actions.size) { "Song Actions should be unique" }

            return SongActionsDialogFragment().apply {
                arguments = if (arguments == null) Bundle() else arguments
                arguments!!.putParcelableArrayList(Extras.SONG_ACTIONS, arrayListOf(*actions))
            }
        }
    }
}