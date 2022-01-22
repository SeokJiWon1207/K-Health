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

    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    private val healthListFragment = HealthListFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHealthBinding.bind(view)

        moveHealthList()
        setDateToday()


    }

    private fun setDateToday() {
        binding.healthCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String = if (month > 10) "${month+1}" else String.format("%02d", month+1)
            val dayOfMonthString: String = if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)

            Log.d("health", "${year}/${month}/${dayOfMonthString}")

            binding.todayDateTextView.text = "${year}/${monthString}/${dayOfMonthString}"
        }
    }

    private fun moveHealthList() {
        binding.healthListFloatingButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(healthListFragment)
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayDate = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}