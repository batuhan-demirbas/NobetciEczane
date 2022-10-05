package com.batuhandemirbas.nobetcieczane

import android.os.Bundle
import com.ace1ofspades.fragmentnavigation.BaseFragmentActivityBinding

import com.batuhandemirbas.nobetcieczane.databinding.ActivityMainBinding

class MainActivity : BaseFragmentActivityBinding<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.bottomNavigation
        tab1.fragment.startDestination = R.id.mapFragment
        tab1.fragment.graphId = R.navigation.nav_graph
        tab2.fragment.startDestination = R.id.nearestFragment
        tab2.fragment.graphId = R.navigation.nav_graph
        tab3.fragment.startDestination = R.id.nearestFragment
        tab3.fragment.graphId = R.navigation.nav_graph
        tab4.fragment.startDestination = R.id.nearestFragment
        tab4.fragment.graphId = R.navigation.nav_graph
        containerId = R.id.navHostFragment
    }
}