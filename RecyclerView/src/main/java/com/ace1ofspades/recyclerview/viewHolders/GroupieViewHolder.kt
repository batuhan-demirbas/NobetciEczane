package com.ace1ofspades.recyclerview.viewHolders

import androidx.viewbinding.ViewBinding



class GroupieViewHolder<T : ViewBinding> : ViewHolder {

    lateinit var binding: T

    constructor(binding: T) : super(binding.root) {
        this.binding = binding
    }

}