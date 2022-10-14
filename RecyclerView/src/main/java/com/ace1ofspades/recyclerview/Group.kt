package com.ace1ofspades.recyclerview

import com.ace1ofspades.recyclerview.items.Item
import com.ace1ofspades.recyclerview.observers.GroupDataObserver

interface Group {
    fun getItemCount(): Int

    fun getItem(position: Int): Item<*,*>

    /**
     * Gets the position of a
     * @param item
     * @return
     */
    fun getPosition(item: Item<*,*>): Int
    fun registerGroupDataObserver(groupDataObserver: GroupDataObserver)
    fun unregisterGroupDataObserver(groupDataObserver: GroupDataObserver)
}