package com.example.k_health.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.k_health.databinding.FragmentHelperBinding
import com.google.android.material.tabs.TabLayoutMediator

class HelperFragment : Fragment() {

    private var mBinding: FragmentHelperBinding? = null
    private val binding get() = mBinding!!
    private lateinit var helperViewPagerAdapter: HelperViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentHelperBinding.inflate(inflater, container, false)
        helperViewPagerAdapter = HelperViewPagerAdapter(this)

        // 뷰페이저에 어댑터 연결
        binding.viewPager.adapter = helperViewPagerAdapter

        TabLayoutMediator(binding.helperTabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "AI플래닝"
                1 -> tab.text = "다이어트"
                2 -> tab.text = "영양정보"
            }
        }.attach()

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}