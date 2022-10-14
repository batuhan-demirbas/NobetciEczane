package com.ace1ofspades.recyclerview.observers

import com.ace1ofspades.recyclerview.Group


interface GroupDataObserver {
    fun onChanged(group: Group)
    fun onItemInserted(group: Group, position: Int)
    fun onItemChanged(group: Group, position: Int)
    fun onItemChanged(group: Group, position: Int, payload: Any?)
    fun onItemRemoved(group: Group, position: Int)
    fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int)
    fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int, payload: Any?)
    fun onItemRangeInserted(group: Group, positionStart: Int, itemCount: Int)
    fun onItemRangeRemoved(group: Group, positionStart: Int, itemCount: Int)
    fun onItemMoved(group: Group, fromPosition: Int, toPosition: Int)
}
