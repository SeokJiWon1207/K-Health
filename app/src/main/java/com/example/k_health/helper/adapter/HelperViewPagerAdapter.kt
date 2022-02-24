package com.example.k_health.helper.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.k_health.helper.HelperDietFragment
import com.example.k_health.helper.HelperNutrientFragment
import com.example.k_health.helper.HelperPlanningFragment

class HelperViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HelperPlanningFragment()
            1 -> HelperDietFragment()
            else -> HelperNutrientFragment()
        }
    }

    override fun getItemCount() = 3


}