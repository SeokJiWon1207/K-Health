package com.example.k_health.health

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthBinding

class HealthFragment: Fragment(R.layout.fragment_health) {

    private var binding: FragmentHealthBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthBinding = FragmentHealthBinding.bind(view)
        binding = fragmentHealthBinding

    }
}