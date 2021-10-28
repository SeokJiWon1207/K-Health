package com.example.k_health.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentReportBinding

class ReportFragment: Fragment(R.layout.fragment_report) {

    private var binding: FragmentReportBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentReportBinding = FragmentReportBinding.bind(view)
        binding = fragmentReportBinding

    }
}