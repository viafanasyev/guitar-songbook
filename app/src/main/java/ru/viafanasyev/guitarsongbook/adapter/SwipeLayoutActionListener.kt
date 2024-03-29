package ru.viafanasyev.guitarsongbook.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.viafanasyev.guitarsongbook.layouts.SwipeRevealLayout

open class SwipeLayoutActionListener<T>(
    private val onItemClick: (item: T, position: Int) -> Unit = { _, _ -> },
    private val onItemAction: (item: T, position: Int) -> Unit = { _, _ -> },
    private val onItemDelete: (item: T, position: Int) -> Unit = { _, _ -> },
) : ActionListener<T, SwipeRevealLayout>() {
    private var currentlyOpenedItemView: SwipeRevealLayout? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        currentlyOpenedItemView?.close(true)
        currentlyOpenedItemView = null
    }

    override fun onItemOpen(itemView: SwipeRevealLayout) {
        if (itemView != currentlyOpenedItemView) {
            currentlyOpenedItemView?.close(true)
            currentlyOpenedItemView = itemView
        }
    }

    override fun onItemClose(itemView: SwipeRevealLayout) {
        // `close` is called on scrolling, so we should check that we close the desired item
        if (itemView == currentlyOpenedItemView) {
            currentlyOpenedItemView = null
        }
    }

    override fun onItemClick(itemView: SwipeRevealLayout, item: T, position: Int) {
        currentlyOpenedItemView?.close(true)
        currentlyOpenedItemView = null
        onItemClick(item, position)
    }

    override fun onItemAction(
        itemView: SwipeRevealLayout,
        item: T,
        position: Int,
    ) {
        currentlyOpenedItemView?.close(true)
        currentlyOpenedItemView = null
        onItemAction(item, position)
    }

    override fun onItemDelete(
        itemView: SwipeRevealLayout,
        item: T,
        position: Int,
    ) {
        currentlyOpenedItemView?.close(true)
        currentlyOpenedItemView = null
        onItemDelete(item, position)
    }
}