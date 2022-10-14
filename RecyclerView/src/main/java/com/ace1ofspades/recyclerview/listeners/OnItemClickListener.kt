package com.ace1ofspades.recyclerview.listeners

import android.view.View
import com.ace1ofspades.recyclerview.items.Item

interface OnItemClickListener {
    fun onItemClick(item: Item<*,*>, view: View)
}
