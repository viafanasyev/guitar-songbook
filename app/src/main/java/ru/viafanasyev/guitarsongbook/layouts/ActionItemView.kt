package ru.viafanasyev.guitarsongbook.layouts

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow
import ru.viafanasyev.guitarsongbook.R
import ru.viafanasyev.guitarsongbook.databinding.ActionItemBinding

class ActionItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    init {
        val binding = ActionItemBinding.inflate(LayoutInflater.from(context), this)

        with(
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ActionItemLayout,
                0, 0
            )
        ) {
            try {
                val iconResId = getResourceIdOrThrow(R.styleable.ActionItemLayout_icon)
                val title = getStringOrThrow(R.styleable.ActionItemLayout_title)

                binding.actionItemIcon.setImageDrawable(AppCompatResources.getDrawable(context, iconResId))
                binding.actionItemTitle.text = title
            } finally {
                recycle()
            }
        }

        // Forced to set `selectableItemBackground` programmatically, because we use `merge` tag in layout
        with(TypedValue()) {
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
            binding.root.setBackgroundResource(resourceId)
        }
    }
}