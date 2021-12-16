package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HealthFragment : Fragment(R.layout.fragment_health), TimeInterface {

    private var binding: FragmentHealthBinding? = null
    private val healthListFragment = HealthListFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthBinding = FragmentHealthBinding.bind(view)
        binding = fragmentHealthBinding

        moveHealthList()

        binding!!.todayDateTextView.text = timeGenerator()

        binding!!.healthCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val dayOfMonthString: String = if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)

            Log.d("health", "${year}/${month}/${dayOfMonthString}")

            binding!!.todayDateTextView.text = "${year}/${month+1}/${dayOfMonthString}"
        }

    }

    private fun moveHealthList() {
        binding!!.healthListFloatingButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(healthListFragment)
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return todayNow
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}