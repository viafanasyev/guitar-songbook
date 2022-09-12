package ru.viafanasyev.guitarsongbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.ui.actions.SongAction
import ru.viafanasyev.guitarsongbook.ui.actions.SongActionType

class SongActionsRecyclerAdapter(
    private val actions: List<SongAction>,
    private val onActionClick: (SongActionType) -> Unit,
) : RecyclerView.Adapter<SongActionsRecyclerAdapter.SongActionViewHolder>() {

    class SongActionViewHolder(
        actionView: View,
        private val actionType: SongActionType
    ) : RecyclerView.ViewHolder(actionView) {
        fun bind(onActionClick: (SongActionType) -> Unit) {
            itemView.setOnClickListener {
                onActionClick(actionType)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongActionViewHolder {
        val (actionType, layoutResId) = actions[viewType]
        val actionView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return SongActionViewHolder(actionView, actionType)
    }

    override fun onBindViewHolder(holder: SongActionViewHolder, position: Int) {
        holder.bind(onActionClick)
    }

    override fun getItemCount(): Int {
        return actions.size
    }
}