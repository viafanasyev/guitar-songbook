package ru.viafanasyev.guitarsongbook.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class ActionListener<T, V> : RecyclerView.OnScrollListener() {
    abstract override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    abstract fun onItemOpen(itemView: V)
    abstract fun onItemClose(itemView: V)
    abstract fun onItemClick(itemView: V, item: T, position: Int)
    abstract fun onItemAction(itemView: V, item: T, position: Int)
    abstract fun onItemDelete(itemView: V, item: T, position: Int)
}