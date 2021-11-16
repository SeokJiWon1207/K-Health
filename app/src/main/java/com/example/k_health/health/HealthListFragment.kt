package com.example.k_health.health

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthlistBinding

class HealthListFragment: Fragment(R.layout.fragment_healthlist) {

    private var binding: FragmentHealthlistBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthListBinding = FragmentHealthlistBinding.bind(view)
        binding = fragmentHealthListBinding

    }
}