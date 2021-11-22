package com.example.k_health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.k_health.databinding.ActivityMainBinding
import com.example.k_health.food.FoodFragment
import com.example.k_health.health.HealthFragment
import com.example.k_health.helper.HelperFragment
import com.example.k_health.home.HomeFragment
import com.example.k_health.report.ReportFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    /*init {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
    }*/

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val helperFragment = HelperFragment()
        val healthFragment = HealthFragment()
        val foodFragment = FoodFragment()
        val reportFragment = ReportFragment()


        // 초기화면 설정
        replaceFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.helper -> replaceFragment(helperFragment)
                R.id.health -> replaceFragment(healthFragment)
                R.id.food -> replaceFragment(foodFragment)
                R.id.report -> replaceFragment(reportFragment)
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