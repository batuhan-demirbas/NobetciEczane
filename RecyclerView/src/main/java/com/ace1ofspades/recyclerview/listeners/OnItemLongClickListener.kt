package com.ace1ofspades.recyclerview.listeners

import android.view.View
import com.ace1ofspades.recyclerview.items.Item


interface OnItemLongClickListener {
    fun onItemLongClick(item: Item<*,*>, view: View): Boolean
}