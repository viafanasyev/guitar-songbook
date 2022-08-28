package ru.viafanasyev.guitarsongbook.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.layouts.SwipeRevealLayout

abstract class ActionListener<T, V, AT> : RecyclerView.OnScrollListener() {
    abstract override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    abstract fun onItemOpen(itemView: V)
    abstract fun onItemClose(itemView: V)
    abstract fun onItemClick(itemView: V, item: T, position: Int)
    abstract fun onItemButtonClick(itemView: V, item: T, position: Int, actionType: AT)
}