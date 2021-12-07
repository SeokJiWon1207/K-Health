package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.format.DateTimeFormatter

class HealthFragment : Fragment(R.layout.fragment_health) {

    private var binding: FragmentHealthBinding? = null
    private val healthListFragment = HealthListFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthBinding = FragmentHealthBinding.bind(view)
        binding = fragmentHealthBinding

        moveHealthList(binding!!.healthListFloatingButton)

        binding!!.healthCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val dayOfMonthString: String = if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)

            Log.d("health", "${year}/${month}/${dayOfMonthString}")

            binding!!.todayDateTextView.text = "${year}/${month+1}/${dayOfMonthString}"
        }

    }

    private fun moveHealthList(floatingActionButton: FloatingActionButton) {
        floatingActionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(healthListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}