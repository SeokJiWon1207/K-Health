package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HelperFragment: Fragment(R.layout.fragment_helper) {

    private var binding: FragmentHelperBinding? = null
    private var helperViewPagerAdapter = HelperViewPagerAdapter(this)
    private val helperPlanningFragment = HelperPlanningFragment()
    private val helperDietFragment = HelperDietFragment()
    private val helperNutrientFragment = HelperNutrientFragment()
    private val childFragment = childFragmentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperBinding = FragmentHelperBinding.bind(view)
        binding = fragmentHelperBinding


        /*val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.viewPager, helperPlanningFragment).commit()*/
//
        // 탭 설정
        /*binding!!.helperTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == binding!!.helperTabLayout.getTabAt(0)) {
                    childFragment.beginTransaction()
                        .replace(R.id.viewPager, helperPlanningFragment)
                        .addToBackStack(null)
                        .commit()
                } else if (tab == binding!!.helperTabLayout.getTabAt(1)) {
                    childFragment.beginTransaction()
                        .replace(R.id.viewPager, helperDietFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    childFragment.beginTransaction()
                        .replace(R.id.viewPager, helperNutrientFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭이 선택되지 않은 상태로 변경 되었을 때
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택 되었을 때
            }
        })

        // 뷰페이저에 어댑터 연결
        binding!!.viewPager.adapter = helperViewPagerAdapter

        *//* 탭과 뷰페이저를 연결, 여기서 새로운 탭을 다시 만드므로 레이아웃에서 꾸미지말고
        여기서 꾸며야함
         *//*
        TabLayoutMediator(binding!!.helperTabLayout, binding!!.viewPager) {tab, position ->
            when(position) {
                0 -> tab.text = "AI플래닝"
                1 -> tab.text = "다이어트"
                2 -> tab.text = "영양정보"
            }
        }.attach()*/


    }
}