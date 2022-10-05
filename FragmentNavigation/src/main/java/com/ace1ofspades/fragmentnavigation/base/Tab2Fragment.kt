package com.ace1ofspades.fragmentnavigation.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ace1ofspades.fragmentnavigation.R
import com.ace1ofspades.fragmentnavigation.Tabs


class Tab2Fragment() : TabFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_tab2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graphId?.let {
            val navController = activity?.findNavController(R.id.tab2fragment)
            val navGraph = navController?.navInflater?.inflate(it)
            startDestination?.let { id ->
                navGraph?.setStartDestination(startDestination!!)
            }
            if (navGraph != null) {
                navController.graph = navGraph
            }
        }
    }

}