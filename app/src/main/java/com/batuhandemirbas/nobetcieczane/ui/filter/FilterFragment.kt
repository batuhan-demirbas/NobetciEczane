package com.batuhandemirbas.nobetcieczane.ui.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.batuhandemirbas.nobetcieczane.databinding.FragmentFilterBinding
import com.google.android.material.snackbar.Snackbar

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("FİLTER OnViewCreated")

    }

    override fun onResume() {
        super.onResume()
        println("FİLTER onResume")

        binding.applyButton.setOnClickListener {
            val city = binding.cityTextField.text.toString()
            val county = binding.countyTextField.text.toString()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}