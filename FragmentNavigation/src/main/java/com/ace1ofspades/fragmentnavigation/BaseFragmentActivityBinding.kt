package com.ace1ofspades.fragmentnavigation

import androidx.viewbinding.ViewBinding

open class BaseFragmentActivityBinding<T : ViewBinding> : BaseFragmentActivity() {

    protected lateinit var binding: T

}