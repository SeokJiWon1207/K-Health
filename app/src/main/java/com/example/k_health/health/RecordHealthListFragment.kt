package com.example.k_health.health

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentRecordHealthlistBinding

class RecordHealthListFragment : Fragment(R.layout.fragment_record_healthlist) {

    private var binding: FragmentRecordHealthlistBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentRecordHealthlistBinding = FragmentRecordHealthlistBinding.bind(view)
        binding = fragmentRecordHealthlistBinding

    }
}