package com.example.k_health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.k_health.databinding.ActivityMainBinding
import com.example.k_health.food.FoodFragment
import com.example.k_health.health.HealthFragment
import com.example.k_health.helper.HelperFragment
import com.example.k_health.home.HomeFragment
import com.example.k_health.sns.SnsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Main","is login")

        val homeFragment = HomeFragment()
        val helperFragment = HelperFragment()
        val healthFragment = HealthFragment()
        val foodFragment = FoodFragment()
        val snsFragment = SnsFragment()


        // 초기화면 설정
        replaceFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.helper -> replaceFragment(helperFragment)
                R.id.health -> replaceFragment(healthFragment)
                R.id.food -> replaceFragment(foodFragment)
                R.id.sns -> replaceFragment(snsFragment)
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
    }



}