package com.ace1ofspades.recyclerview.viewHolders

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ace1ofspades.recyclerview.items.Item

import com.ace1ofspades.recyclerview.Group

import com.ace1ofspades.recyclerview.observers.GroupDataObserver
import com.ace1ofspades.recyclerview.providers.SpanSizeProvider
import com.ace1ofspades.recyclerview.viewHolders.ViewHolder
import com.ace1ofspades.recyclerview.listeners.OnItemClickListener
import com.ace1ofspades.recyclerview.listeners.OnItemLongClickListener

open class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    var item: Item<*,*>? = null


    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    private val onClickListener =
        View.OnClickListener { v -> // Discard click if the viewholder has been removed, but was still in the process of
            // animating its removal while clicked (unlikely, but technically possible)
            if (onItemClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                onItemClickListener!!.onItemClick(item!!, v)
            }
        }
    private val onLongClickListener =
        View.OnLongClickListener { v -> // Discard long click if the viewholder has been removed, but was still in the process of
            // animating its removal while long clicked (unlikely, but technically possible)
            if (onItemLongClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                onItemLongClickListener!!.onItemLongClick(item!!, v)
            } else false
        }

    fun bind(
        item: Item<*,*>,
        onItemClickListener: OnItemClickListener?,
        onItemLongClickListener: OnItemLongClickListener?
    ) {
        this.item = item

        // Only set the top-level click listeners if a) they exist, and b) the item has
        // clicks enabled.  This ensures we don't interfere with user-set click listeners.

        // It would be nice to keep our listeners always attached and set them only once on creating
        // the viewholder, but different items of the same layout type may not have the same click
        // listeners or even agree on whether they are clickable.
        if (onItemClickListener != null && item.isClickable) {
            itemView.setOnClickListener(onClickListener)
            this.onItemClickListener = onItemClickListener
        }
        if (onItemLongClickListener != null && item.isLongClickable) {
            itemView.setOnLongClickListener(onLongClickListener)
            this.onItemLongClickListener = onItemLongClickListener
        }
    }

    fun unbind() {
        // Only set the top-level click listener to null if we had previously set it ourselves.

        // This avoids undoing any click listeners the user may set which might be persistent for
        // the life of the viewholder. (It's up to the user to make sure that's correct behavior.)
        if (onItemClickListener != null && item!!.isClickable) {
            itemView.setOnClickListener(null)
        }
        if (onItemLongClickListener != null && item!!.isLongClickable) {
            itemView.setOnLongClickListener(null)
        }
        item = null
        onItemClickListener = null
        onItemLongClickListener = null
    }

    val extras: Map<String, Any>
        get() = item!!.extras
    val swipeDirs: Int
        get() = item!!.swipeDirs
    val dragDirs: Int
        get() = item!!.dragDirs
    val context: Context
        get() = itemView.context
    val root: View
        get() = itemView
}
