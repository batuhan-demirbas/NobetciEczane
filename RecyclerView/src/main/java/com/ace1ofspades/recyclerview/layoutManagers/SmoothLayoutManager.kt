package com.ace1ofspades.recyclerview.layoutManagers

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.ace1ofspades.recyclerview.enums.SnapPreference


class SmoothLayoutManager : GridLayoutManager {

    constructor(context: Context,spanCount: Int) : super(context,spanCount)
    constructor(context: Context, spanCount: Int,orientation: Int, reverseLayout: Boolean) : super(context, spanCount,orientation, reverseLayout)
    constructor(
        context: Context,
        spanCount: Int,
        orientation: Int,
        snapPreference: SnapPreference,
        reverseLayout: Boolean) : super(context,spanCount, orientation, reverseLayout) {
        this.snapPreference = snapPreference
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    private var snapPreference: SnapPreference = SnapPreference.LEFT
    private var speed:Int? = null
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {

        speed?.let {

            val animationScroller = InfiniteAnimationScroller(recyclerView.context, it)
            animationScroller.targetPosition = position
            startSmoothScroll(animationScroller)
            return
        }

        val smoothScroller = SmoothSnapScroller(recyclerView.context, snapPreference)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)

    }

    fun animate(recyclerView: RecyclerView, position: Int, speed: Int) {
        this.speed = speed * -1
        smoothScrollToPosition(recyclerView, object: RecyclerView.State(){}, position)
    }

    fun smoothScrollToPosition(recyclerView: RecyclerView, position: Int) {
        smoothScrollToPosition(recyclerView, object: RecyclerView.State(){}, position)
    }

    private class SmoothSnapScroller(context: Context, val snapPreference: SnapPreference): LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return snapPreference.value
        }

        override fun getHorizontalSnapPreference(): Int {
            return snapPreference.value
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 120f / displayMetrics.densityDpi
        }

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            if (this.snapPreference.value == 0) {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);

            }
            return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
        }
    }
    private class InfiniteAnimationScroller(context: Context, val speed:Int) : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return super.calculateSpeedPerPixel(displayMetrics) + 0.3f
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_ANY
        }

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return speed
        }

    }

}