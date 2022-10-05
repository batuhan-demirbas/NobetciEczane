package com.ace1ofspades.fragmentnavigation

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.viewbinding.ViewBinding
import com.ace1ofspades.fragmentnavigation.base.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

open class BaseFragmentActivity() : AppCompatActivity() {


    val tab1: Tabs = Tabs.Tab1
    val tab2: Tabs = Tabs.Tab2
    val tab3: Tabs = Tabs.Tab3
    val tab4: Tabs = Tabs.Tab4
    val tab5: Tabs = Tabs.Tab5

    val fm: FragmentManager = supportFragmentManager

    var activeTab = tab1

    var containerId: Int? = null
        get() {return field}
        set(value) {

            value?.let {
                var transaction = fm.beginTransaction()

                if ((navView?.menu?.size() ?: 0) > 0 ) {
                    transaction.add(it, tab1.fragment, tab1.tag)
                    if (activeTab != tab1) transaction.hide(tab1.fragment)
                }

                if ((navView?.menu?.size() ?: 0) > 1 ) {
                    transaction.add(it, tab2.fragment, tab2.tag)
                    if (activeTab != tab2) transaction.hide(tab2.fragment)
                }

                if ((navView?.menu?.size() ?: 0) > 2 ) {
                    transaction.add(it, tab3.fragment, tab3.tag)
                    if (activeTab != tab3) transaction.hide(tab3.fragment)
                }

                if ((navView?.menu?.size() ?: 0) > 3 ) {
                    transaction.add(it, tab4.fragment, tab4.tag)
                    if (activeTab != tab4) transaction.hide(tab4.fragment)
                }

                if ((navView?.menu?.size() ?: 0) > 4 ) {
                    transaction.add(it, tab5.fragment, tab5.tag)
                    if (activeTab != tab5) transaction.hide(tab5.fragment)
                }

                transaction.commit()
            }
            field = value
        }


    var currentIndex: Int
        get() {return activeTab.index}
        set(value) {
        val selectedItem = tabsList[value]

        if (activeTab != selectedItem) {
            newActiveTab = selectedItem
        }

        newActiveTab?.let {
            fm.beginTransaction().hide(activeTab.fragment).show(it.fragment).commit()
            activeTab = it
            newActiveTab = null
        }
    }

    private var newActiveTab:Tabs? = null

    var tabsList: List<Tabs> = listOf()

    var navView: BottomNavigationView? = null
        get() {return field}
        set(value) {
            for (i in 0 until (value?.menu?.size() ?: 0)) {
                value?.menu?.getItem(i)?.itemId?.let {
                    tabsList[i].menuItem = it
                }
            }
            value?.setOnItemSelectedListener(mOnItemSelectedListener)
            value?.setOnItemReselectedListener(mOnItemReselectedListener)
            field = value
        }

    fun setGraphId(tabs: Tabs, id:Int) {
        tabs.fragment.graphId = id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabsList = listOf(tab1, tab2, tab3, tab4, tab5)
        //for (i in tabsList) i.fragment.enterTransition = Fade(Fade.OUT)

        for (i in tabsList) i.fragment.graphId = i.navigationId

    }

    private val mOnItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->

        val selectedItem = tabsList.first {
            it.menuItem == item.itemId
        }

        if (activeTab != selectedItem) {
            newActiveTab = selectedItem
        }

        newActiveTab?.let {
            fm.beginTransaction().hide(activeTab.fragment).show(it.fragment).commit()
            activeTab = it
            newActiveTab = null
            return@OnItemSelectedListener true
        }
        return@OnItemSelectedListener false
    }
    private val mOnItemReselectedListener = NavigationBarView.OnItemReselectedListener { item ->
        val controller = findNavController(this, activeTab.container)
        if (controller.currentBackStackEntry != null) {
            controller.popBackStack()
            return@OnItemReselectedListener
        }
        return@OnItemReselectedListener
    }

    override fun onBackPressed() {
        val controller = findNavController(this, activeTab.container)
        if (controller.currentBackStackEntry != null && controller.backQueue.size > 2) {
            controller.popBackStack()
            return
        }
        super.onBackPressed()
    }
}