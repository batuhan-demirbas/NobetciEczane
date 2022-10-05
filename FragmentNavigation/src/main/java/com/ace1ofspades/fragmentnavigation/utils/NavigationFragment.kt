package com.ace1ofspades.fragmentnavigation.utils

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform


open class NavigationFragment<T : ViewBinding> : Fragment() {

    protected var _binding: T? = null

    val binding get() = _binding!!
    var drawingId: Int = -1
    var transitionName: String? = null

    var isFromFragment: Boolean = false
    var transitionItem: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enterTransition = androidx.transition.Slide(Gravity.BOTTOM)
        //exitTransition =  androidx.transition.Slide(Gravity.BOTTOM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        /*if (isFromFragment) {
            postponeEnterTransition()
            root.doOnPreDraw { startPostponedEnterTransition() }
        } else {
            arguments?.let {
                transitionName = it.getString("transitionName")
                transitionItem?.transitionName = transitionName
                val transform = MaterialContainerTransform().apply {
                    drawingViewId = drawingId
                    duration = 375
                    scrimColor = Color.WHITE
                }

                sharedElementEnterTransition = transform
                sharedElementReturnTransition = transform
            }
        }*/

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*if (isFromFragment) {
            val transform = MaterialContainerTransform().apply {
                drawingViewId = drawingId
                duration = 375
                scrimColor = Color.WHITE
            }

            returnTransition = transform
            exitTransition = Hold()
            //enterTransition = Hold()
        }*/
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun startFragment(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    fun startFragmentWithtransitionName(
        directions: NavDirections,
        view: View?,
        arguments: ((arguments: Bundle) -> Unit)? = null
    ) {

        view?.let {

            val extras = FragmentNavigatorExtras(it to it.transitionName)

            directions.arguments.putString("transitionName", it.transitionName)
            arguments?.let {
                arguments(directions.arguments)
            }


            findNavController().navigate(directions, extras)
        }

    }
}
