package ru.viafanasyev.guitarsongbook.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.layouts.SwipeRevealLayout

abstract class ActionListener<T> : RecyclerView.OnScrollListener() {
    abstract override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    abstract fun onItemOpen(itemView: SwipeRevealLayout)
    abstract fun onItemClose(itemView: SwipeRevealLayout)
    abstract fun onItemClick(itemView: SwipeRevealLayout, item: T, position: Int)
}