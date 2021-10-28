package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperBinding

class HelperFragment: Fragment(R.layout.fragment_helper) {

    private var binding: FragmentHelperBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperBinding = FragmentHelperBinding.bind(view)
        binding = fragmentHelperBinding

    }
}