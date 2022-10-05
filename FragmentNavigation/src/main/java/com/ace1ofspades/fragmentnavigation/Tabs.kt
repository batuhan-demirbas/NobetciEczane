package com.ace1ofspades.fragmentnavigation

import androidx.fragment.app.Fragment
import com.ace1ofspades.fragmentnavigation.base.*

enum class Tabs(
    val index:Int,
    val fragment: TabFragment,
    val container:Int,
    var navigationId:Int,
    val tag:String,
    var menuItem: Int) {
    Tab1(0, Tab1Fragment(), R.id.tab1fragment, R.navigation.navigation_tab,"TAB1_TAG", R.id.tab1fragmentItem),
    Tab2(1, Tab2Fragment(), R.id.tab2fragment, R.navigation.navigation_tab,"TAB2_TAG", R.id.tab2fragmentItem),
    Tab3(2, Tab3Fragment(), R.id.tab3fragment, R.navigation.navigation_tab,"TAB3_TAG", R.id.tab3fragmentItem),
    Tab4(3, Tab4Fragment(), R.id.tab4fragment, R.navigation.navigation_tab,"TAB4_TAG", R.id.tab4fragmentItem),
    Tab5(4, Tab5Fragment(), R.id.tab5fragment, R.navigation.navigation_tab,"TAB5_TAG", R.id.tab5fragmentItem)
}