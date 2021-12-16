package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperPlanningBinding

class HelperPlanningFragment: Fragment(R.layout.fragment_helper_planning) {

    private var binding: FragmentHelperPlanningBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperPlanningBinding = FragmentHelperPlanningBinding.bind(view)
        binding = fragmentHelperPlanningBinding

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}