package com.ace1ofspades.recyclerview.items

import android.view.View
import androidx.viewbinding.ViewBinding
import com.ace1ofspades.recyclerview.viewHolders.GroupieViewHolder




abstract class BindableItem<T : ViewBinding> : Item<GroupieViewHolder<T>?,T> {

    protected constructor(id:Long) : super(id)

    constructor(): super()

    protected abstract fun initializeViewBinding(view: View): T


    override fun createViewHolder(itemView: View): GroupieViewHolder<T> {
        val binding = initializeViewBinding(itemView)
        return GroupieViewHolder(binding)

    }


    override fun bind(viewHolder: GroupieViewHolder<T>?, position: Int) {
        if (viewHolder != null) {
            bind(viewHolder.binding,position)
        }
    }


    /**
     * Perform any actions required to set up the view for display.
     *
     * @param viewBinding The ViewBinding to bind
     * @param position The adapter position
     */
    abstract fun bind(binding: T, position: Int)

    /**
     * Perform any actions required to set up the view for display.
     *
     * If you don't specify how to handle payloads in your implementation, they'll be ignored and
     * the adapter will do a full rebind.
     *
     * @param viewBinding The ViewBinding to bind
     * @param position The adapter position
     * @param payloads A list of payloads (may be empty)
     */
}