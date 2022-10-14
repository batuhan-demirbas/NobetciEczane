package com.ace1ofspades.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.ace1ofspades.recyclerview.items.Item
import com.ace1ofspades.recyclerview.listeners.OnItemClickListener
import com.ace1ofspades.recyclerview.listeners.OnItemLongClickListener
import com.ace1ofspades.recyclerview.observers.GroupDataObserver
import com.ace1ofspades.recyclerview.viewHolders.ViewHolder


class GroupAdapter<VH : ViewHolder> : RecyclerView.Adapter<VH>() , GroupDataObserver {

    private val groups: MutableList<Group?> = mutableListOf()
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    var spanCount = 1
    private var lastItemForViewTypeLookup: Item<VH,*>? = null
    val spanSizeLookup: GridLayoutManager.SpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return try {
                    this@GroupAdapter.getItem(position).getSpanSize(spanCount, position)
                } catch (var3: IndexOutOfBoundsException) {
                    spanCount
                }
            }
        }

    private val listUpdateCallback: ListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            this@GroupAdapter.notifyItemRangeChanged(position, count, payload)
        }
    }


    fun getItems():MutableList<Group?>{

        return groups

    }

    fun update(newGroups: MutableList<Group?>) {

        val oldGroups: MutableList<Group?> = groups
        val oldBodyItemCount = getItemCount(oldGroups)
        val newBodyItemCount = getItemCount(newGroups)
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldBodyItemCount
            }

            override fun getNewListSize(): Int {
                return newBodyItemCount
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = getItem(oldGroups, oldItemPosition)
                val newItem = getItem(newGroups, newItemPosition)
                return newItem.isSameAs(oldItem)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = getItem(oldGroups, oldItemPosition)
                val newItem = getItem(newGroups, newItemPosition)
                return newItem == oldItem
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = getItem(oldGroups, oldItemPosition)
                val newItem = getItem(newGroups, newItemPosition)
                return oldItem.getChangePayload(newItem)
            }
        })
        var var6: Iterator<*> = groups.iterator()
        var group: Group
        while (var6.hasNext()) {
            group = var6.next() as Group
            group.unregisterGroupDataObserver(this)
        }
        groups.clear()
        groups.addAll(newGroups)
        var6 = newGroups.iterator()
        while (var6.hasNext()) {
            group = var6.next() as Group
            group.registerGroupDataObserver(this)
        }
        diffResult.dispatchUpdatesTo(listUpdateCallback)
    }


    fun setOnItemClickListener(onItemClickListener: (item: Item<*,*>, view: View)->Unit) {
        this.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: Item<*,*>, view: View) {
                onItemClickListener(item,view)
            }

        }
    }


    fun setOnItemLongClickListener(onItemLongClickListener: (item: Item<*,*>, view: View)->Boolean) {
        this.onItemLongClickListener = object : OnItemLongClickListener {
            override fun onItemLongClick(item: Item<*,*>, view: View): Boolean {
                return onItemLongClickListener(item, view)
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }



    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, layoutResId: Int): VH {
        val inflater = LayoutInflater.from(parent.context)

        val item = getItemForViewType(layoutResId)
        val itemView = inflater.inflate(layoutResId, parent, false)

        return item!!.createViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {}
    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        val contentItem = this.getItem(position)
        contentItem.bind(holder, position, payloads, onItemClickListener, onItemLongClickListener)
    }

    override fun onViewRecycled(holder: VH) {
        holder.unbind()
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        val contentItem = holder.item
        return contentItem?.isRecyclable ?: false
    }

    override fun getItemViewType(position: Int): Int {
        lastItemForViewTypeLookup = this.getItem(position)
        return if (lastItemForViewTypeLookup == null) {
            throw RuntimeException("Invalid position $position")
        } else {
            lastItemForViewTypeLookup!!.getLayout()
        }
    }

    fun getItem(holder: VH): Item<*,*>? {
        return holder?.item
    }

    fun getItem(position: Int): Item<VH,*> {
        return getItem(groups, position) as Item<VH,*>
    }

    fun getAdapterPosition(contentItem: Item<*,*>): Int {
        var count = 0
        var group: Group
        val var3: Iterator<*> = groups.iterator()
        while (var3.hasNext()) {
            group = var3.next() as Group
            val index = group.getPosition(contentItem)
            if (index >= 0) {
                return index + count
            }
            count += group.getItemCount()
        }
        return -1
    }

    fun getAdapterPosition(group: Group): Int {
        val index = groups.indexOf(group)
        return if (index == -1) {
            -1
        } else {
            var position = 0
            for (i in 0 until index) {
                position += groups[i]!!.getItemCount()
            }
            position
        }
    }

    override fun getItemCount(): Int {
        return getItemCount(groups)
    }

    fun getItemCount(groupIndex: Int): Int {
        return if (groupIndex >= groups.size) {
            throw IndexOutOfBoundsException("Requested group index " + groupIndex + " but there are " + groups.size + " groups")
        } else {
            groups[groupIndex]!!.getItemCount()
        }
    }

    fun clear() {
        val var1: Iterator<*> = groups.iterator()
        while (var1.hasNext()) {
            val group = var1.next() as Group
            group.unregisterGroupDataObserver(this)
        }
        groups.clear()
        notifyDataSetChanged()
    }

    fun addForInfinite(group: Group) {
        val itemCountBeforeGroup = this.itemCount
        group.registerGroupDataObserver(this)
        groups.add(group)
        notifyItemRangeInserted(itemCountBeforeGroup, group.getItemCount())
    }

    var arrayCount = 0

    fun add(group: Group) {
        val itemCountBeforeGroup = this.itemCount
        group.registerGroupDataObserver(this)
        groups.add(group)
        arrayCount++
        notifyItemRangeInserted(itemCountBeforeGroup, group.getItemCount())
    }

    fun addAll(groups: Collection<Group?>) {
        val itemCountBeforeGroup = this.itemCount
        var additionalSize = 0
        val var4: Iterator<*> = groups.iterator()
        while (var4.hasNext()) {
            val group = var4.next() as Group
            additionalSize += group.getItemCount()
            group.registerGroupDataObserver(this)
        }
        this.groups.addAll(groups)
        notifyItemRangeInserted(itemCountBeforeGroup, additionalSize)
    }

    fun remove(group: Group) {
        arrayCount--
        val position = groups.indexOf(group)
        this.remove(position, group)
    }

    fun removeAll(groups: Collection<Group?>) {
        val var2: Iterator<*> = groups.iterator()
        while (var2.hasNext()) {
            val group = var2.next() as Group
            this.remove(group)
        }
    }

    fun removeGroup(index: Int) {
        val group = this.getGroup(index)
        this.remove(index, group)
    }

    private fun remove(position: Int, group: Group) {
        val itemCountBeforeGroup = getItemCountBeforeGroup(position)
        group.unregisterGroupDataObserver(this)
        groups.removeAt(position)
        notifyItemRangeRemoved(itemCountBeforeGroup, group.getItemCount())
    }

    fun add(index: Int, group: Group) {
        if (group == null) {
            throw RuntimeException("Group cannot be null")
        } else {
            group.registerGroupDataObserver(this)
            groups.add(index, group)
            val itemCountBeforeGroup = getItemCountBeforeGroup(index)
            notifyItemRangeInserted(itemCountBeforeGroup, group.getItemCount())
        }
    }

    private fun getGroup(position: Int): Group {
        var previous = 0
        var group: Group
        val var4: Iterator<*> = groups.iterator()
        while (var4.hasNext()) {
            group = var4.next() as Group
            val size = group.getItemCount()
            if (position - previous < size) {
                return group
            }
            previous += group.getItemCount()
        }
        throw IndexOutOfBoundsException("Requested position " + position + "in group adapter but there are only " + previous + " items")
    }

    private fun getItemCountBeforeGroup(groupIndex: Int): Int {
        var count = 0
        var group: Group
        val var3: Iterator<*> = groups.subList(0, groupIndex).iterator()
        while (var3.hasNext()) {
            group = var3.next() as Group
            count += group.getItemCount()
        }
        return count
    }

    fun getGroup(contentItem: Item<*,*>?): Group {
        val var2: Iterator<*> = groups.iterator()
        var group: Group
        do {
            if (!var2.hasNext()) {
                throw IndexOutOfBoundsException("Item is not present in adapter or in any group")
            }
            group = var2.next() as Group
        } while (group.getPosition(contentItem!!) < 0)
        return group
    }

    fun getGroups(): ArrayList<Group?> {
        val result: ArrayList<Group?> = arrayListOf()
        for (i in groups) {
            result.add(i)
        }
        return result
    }

    override fun onChanged(group: Group) {
        this.notifyItemRangeChanged(this.getAdapterPosition(group), group.getItemCount())
    }

    override fun onItemInserted(group: Group, position: Int) {
        notifyItemInserted(this.getAdapterPosition(group) + position)
    }

    override fun onItemChanged(group: Group, position: Int) {
        this.notifyItemChanged(this.getAdapterPosition(group) + position)
    }


    override fun onItemChanged(group: Group, position: Int, payload: Any?) {
        this.notifyItemChanged(this.getAdapterPosition(group) + position, payload)
    }

    override fun onItemRemoved(group: Group, position: Int) {
        notifyItemRemoved(this.getAdapterPosition(group) + position)
    }

    override fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int) {
        this.notifyItemRangeChanged(this.getAdapterPosition(group) + positionStart, itemCount)
    }

    override fun onItemRangeChanged(
        group: Group,
        positionStart: Int,
        itemCount: Int,
        payload: Any?
    ) {
        this.notifyItemRangeChanged(
            this.getAdapterPosition(group) + positionStart,
            itemCount,
            payload
        )
    }


    override fun onItemRangeInserted(group: Group, positionStart: Int, itemCount: Int) {
        notifyItemRangeInserted(this.getAdapterPosition(group) + positionStart, itemCount)
    }

    override fun onItemRangeRemoved(group: Group, positionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(this.getAdapterPosition(group) + positionStart, itemCount)
    }

    override fun onItemMoved(group: Group, fromPosition: Int, toPosition: Int) {
        val GroupAdapterPosition = this.getAdapterPosition(group)
        notifyItemMoved(GroupAdapterPosition + fromPosition, GroupAdapterPosition + toPosition)
    }

    private fun getItemForViewType(@LayoutRes layoutResId: Int): Item<VH,*>? {
        return if (lastItemForViewTypeLookup != null && lastItemForViewTypeLookup!!.getLayout() == layoutResId) {
            this.lastItemForViewTypeLookup
        } else {
            for (i in 0 until this.itemCount) {
                val item = this.getItem(i)
                if (item.getLayout() == layoutResId) {
                    return item
                }
            }
            throw IllegalStateException("Could not find model for view type: $layoutResId")
        }
    }

    companion object {
        private fun getItem(groups: Collection<Group?>, position: Int): Item<*,*> {
            var count = 0
            var group: Group
            val var3: Iterator<*> = groups.iterator()
            while (var3.hasNext()) {
                group = var3.next() as Group
                if (position < count + group.getItemCount()) {
                    return group.getItem(position - count)
                }
                count += group.getItemCount()
            }
            throw IndexOutOfBoundsException("Requested position " + position + "in group adapter but there are only " + count + " items")
        }

        private fun getItemCount(groups: Collection<Group?>): Int {
            var count = 0
            var group: Group
            val var2: Iterator<*> = groups.iterator()
            while (var2.hasNext()) {
                group = var2.next() as Group
                count += group.getItemCount()
            }
            return count
        }
    }
}