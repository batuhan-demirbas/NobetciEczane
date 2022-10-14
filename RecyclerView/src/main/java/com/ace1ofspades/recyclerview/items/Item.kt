package com.ace1ofspades.recyclerview.items

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import com.ace1ofspades.recyclerview.Group
import com.ace1ofspades.recyclerview.listeners.OnItemClickListener
import com.ace1ofspades.recyclerview.listeners.OnItemLongClickListener
import com.ace1ofspades.recyclerview.observers.GroupDataObserver
import com.ace1ofspades.recyclerview.providers.SpanSizeProvider
import com.ace1ofspades.recyclerview.viewHolders.ViewHolder
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicLong

abstract class Item<VH : ViewHolder?,T> : Group, SpanSizeProvider {

    var model:T? = null
    var fragment: Fragment? = null
    private val ID_COUNTER = AtomicLong(0)

    protected var parentDataObserver: GroupDataObserver? = null

    private var id: Long? = null

    val extras: Map<String, Any> = hashMapOf()

    protected constructor(id:Long) {
        this.id = id
    }



    constructor() {
        ID_COUNTER.decrementAndGet()
    }

    open fun createViewHolder(itemView: View): VH {
        return ViewHolder(itemView) as VH
    }

    fun <R:View> findViewById(id:Int) : R {
        return  itemView!!.findViewById(id)
    }

    var context: Context? = null
    var root: View? = null
    var itemView: View? = null
    var viewHolder: VH? = null

    /**
     * Perform any actions required to set up the view for display.
     *
     * @param holder              The viewholder to bind
     * @param position            The adapter position
     * @param payloads            Any payloads (this list may be empty)
     * @param onItemClickListener An optional adapter-level click listener
     * @param onItemLongClickListener An optional adapter-level long click listener
     */
    @CallSuper
    fun bind(
        holder: VH, position: Int, payloads: List<Any?>,
        onItemClickListener: OnItemClickListener?,
        onItemLongClickListener: OnItemLongClickListener?
    ) {
        holder!!.bind(this, onItemClickListener, onItemLongClickListener)
        bind(holder, position, payloads)
    }



    open fun bind(p0: VH, p1: Int) {}

    open fun initialize() {}

    open fun configure() {}

    /**
     * If you don't specify how to handle payloads in your implementation, they'll be ignored and
     * the adapter will do a full rebind.
     *
     * @param holder The ViewHolder to bind
     * @param position The adapter position
     * @param payloads A list of payloads (may be empty)
     */
    @CallSuper
    fun bind(p0: VH, p1: Int, payloads: List<Any?>) {
        context = p0?.context
        root = p0?.root
        itemView = p0?.itemView
        itemView?.let { itemView ->
            itemView.transitionName = UUID.randomUUID().toString()
        }
        viewHolder = p0
        initialize()
        configure()
        bind(p0, p1)
    }

    /**
     * Do any cleanup required for the viewholder to be reused.
     *
     * @param holder The ViewHolder being recycled
     */
    @CallSuper
    fun unbind(holder: VH) {
        holder?.unbind()
    }
    /**
     * Whether the view should be recycled. Return false to prevent the view from being recycled.
     * (Note that it may still be re-bound.)
     *
     * @return Whether the view should be recycled.
     */
    val isRecyclable: Boolean
        get() = true

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }

    val swipeDirs: Int
        get() = 0
    val dragDirs: Int
        get() = 0

    @LayoutRes
    abstract fun getLayout(): Int



    override fun getItemCount(): Int {
        return 1
    }

    override fun getItem(position: Int): Item<*,*> {
        return if (position == 0) {
            this
        } else {
            throw IndexOutOfBoundsException(
                "Wanted item at position " + position + " but" +
                        " an Item is a Group of size 1"
            )
        }
    }

    override fun getPosition(item: Item<*,*>): Int {
        return if (this === item) 0 else -1
    }

    override fun registerGroupDataObserver(groupDataObserver: GroupDataObserver) {
        parentDataObserver = groupDataObserver
    }

    override fun unregisterGroupDataObserver(groupDataObserver: GroupDataObserver) {
        parentDataObserver = null
    }

    val isClickable: Boolean
        get() = true
    val isLongClickable: Boolean
        get() = true

    fun notifyChanged() {
        if (parentDataObserver != null) {
            parentDataObserver!!.onItemChanged(this, 0)
        }
    }

    fun notifyChanged(payload: Any?) {
        if (parentDataObserver != null) {
            parentDataObserver!!.onItemChanged(this, 0, payload)
        }
    }

    /**
     * Whether two item objects represent the same underlying data when compared using DiffUtil,
     * even if there has been a change in that data.
     *
     *
     * The default implementation compares both view type and id.
     */
    fun isSameAs(other: Item<*,*>): Boolean {
        return if (getLayout() != other.getLayout()) {
            false
        } else id == other.id
    }

    fun getChangePayload(newItem: Item<*,*>?): Any? {
        return null
    }

    companion object {
        private val ID_COUNTER = AtomicLong(0)
    }
}
