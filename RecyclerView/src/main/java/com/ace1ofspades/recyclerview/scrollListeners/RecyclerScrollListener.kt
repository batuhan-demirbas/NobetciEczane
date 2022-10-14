package com.ace1ofspades.recyclerview.scrollListeners

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ace1ofspades.recyclerview.GroupAdapter
import com.ace1ofspades.recyclerview.enums.ScrollAttributes
import com.ace1ofspades.recyclerview.layoutManagers.SmoothLayoutManager
import com.ace1ofspades.recyclerview.viewHolders.ViewHolder


open class RecyclerScrollListener(
    val adapter: GroupAdapter<*>,
    val attributes: ScrollAttributes = ScrollAttributes()
): RecyclerView.OnScrollListener() {

    var state = 0
    var oldState = 0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState != 0) {
            oldState = newState
        }
        if (newState == 0) {
            recyclerView.handleInfinite()

            if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
                return
            }
            if(!attributes.animation){
                if (oldState == 2) {
                    recyclerView.handlerState {
                        return@handlerState it > attributes.settlingOffset
                    }
                }else {
                    if (state > 0) {
                        recyclerView.handlerState {
                            return@handlerState it > attributes.iddleOffset
                        }
                    }else {
                        recyclerView.handlerState {
                            return@handlerState it > getRestPercentage(attributes.iddleOffset)
                        }
                    }
                }
            }else{
                recyclerView.handlerState {
                    return@handlerState true
                }

            }


        }
    }
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        state = dx
    }

    private fun RecyclerView.handleInfinite() {
        if (!attributes.isInfinite) {
            return
        }
        val adapter = this@RecyclerScrollListener.adapter
        val position = getPosition(this)
        if ((adapter.arrayCount + position) > adapter.itemCount) {
            var diff = (adapter.itemCount - adapter.arrayCount)

            while (diff <= position && diff>=0) {
                adapter.addForInfinite(adapter.getItem(diff))
                diff++
            }
        }
    }
    private fun RecyclerView.handlerState(condition: (percentage:Int)->Boolean) {

        val layoutManager = getLayoutManager(this)
        var position = layoutManager.findFirstVisibleItemPosition()
        var percentage = getPercentage(position,this)

        if (condition(percentage)) {
            layoutManager.smoothScrollToPosition(this, position)
        }else {
            layoutManager.smoothScrollToPosition(this,position + layoutManager.spanCount)
        }
    }

    private fun getPosition(recyclerView: RecyclerView):Int {
        val layoutManager = getLayoutManager(recyclerView)
        var position = layoutManager.findFirstVisibleItemPosition()
        return position
    }
    private fun getPercentage(i:Int, recyclerView: RecyclerView) : Int {

        val layoutManager = getLayoutManager(recyclerView)

        var rvRect = Rect()
        recyclerView.getGlobalVisibleRect(rvRect)

        val rowRect = Rect()
        layoutManager.findViewByPosition(i)?.getGlobalVisibleRect(rowRect)
        var percentFirst: Int
        percentFirst = if (rowRect.right >= rvRect.right) {
            val visibleHeightFirst = rvRect.right - rowRect.left
            visibleHeightFirst * 100 / (layoutManager.findViewByPosition(i)?.width ?: 1)
        } else {
            val visibleHeightFirst = rowRect.right - rvRect.left
            visibleHeightFirst * 100 / (layoutManager.findViewByPosition(i)?.width ?: 1)
        }
        if (percentFirst > 100) percentFirst = 100

        return percentFirst
    }
    private fun getRestPercentage(percentage:Int):Int {
        return  100 - percentage
    }

    private fun getLayoutManager(recyclerView: RecyclerView) : SmoothLayoutManager {
        val layoutManager = recyclerView.layoutManager as SmoothLayoutManager
        return layoutManager
    }

}

fun RecyclerView.setRowPaging(isInfinite:Boolean) {
    val adapter = (this.adapter as? GroupAdapter<*>) ?: return

    val attrs = ScrollAttributes(isInfinite, 82, 50)
    this.addOnScrollListener(RecyclerScrollListener(adapter, attrs))
}