package com.ace1ofspades.fragmentnavigation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ace1ofspades.fragmentnavigation.R

open class TabFragment:Fragment() {
    var graphId:Int? = null
    var startDestination:Int? = null
    set(value) {
        field = value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}