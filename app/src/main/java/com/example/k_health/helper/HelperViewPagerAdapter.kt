package com.example.k_health.helper

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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