package com.example.k_health.sns

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentSnsWriteBinding

class SnsWriteFragment: Fragment(R.layout.fragment_sns_write) {
    private var _binding: FragmentSnsWriteBinding? = null
    private val binding get() = _binding!!
    private val snsFragment = SnsFragment()
    private val snsWriteFragment = SnsWriteFragment()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSnsWriteBinding.bind(view)

        setToolbar()

    }

    fun setToolbar() = with(binding) {
        toolbar.inflateMenu(R.menu.toolbar_sns)

        // 프래그먼트가 메뉴 관련 콜백을 수신하려 한다고 시스템에 알림
        // 메뉴 관련 이벤트(생성, 클릭 등)가 발생하면 이벤트 처리 메서드가 먼저 활동에서 호출된 후 프래그먼트에서 호출
        setHasOptionsMenu(true)
        toolbar.title = "글쓰기"

        // 액션버튼 클릭 했을 때 이벤트 처리
        toolbar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_home -> {
                    //홈버튼 눌렀을 때
                    (activity as MainActivity).replaceFragment(snsFragment)
                    Log.d(SnsFragment.TAG, "홈 버튼 클릭")
                    true
                }
                R.id.action_write -> {
                    //글쓰기 버튼 눌렀을 때
                    (activity as MainActivity).replaceFragment(snsWriteFragment)
                    Log.d(SnsFragment.TAG, "글쓰기 버튼 클릭")
                    true
                }
                R.id.action_refresh -> {
                    //새로고침 버튼 눌렀을 때
                    Log.d(SnsFragment.TAG, "새로고침 버튼 클릭")
                    true
                }
                else -> false
            }
        }
    }
}