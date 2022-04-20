package com.example.k_health

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.k_health.databinding.ActivityRegisterBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity: AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRegisterButton()

    }

    private fun initRegisterButton() = with(binding) {
        registerButton.setOnClickListener {
            createAccount(emailEditText.text.toString().trim(), passwordEditText.text.toString().trim())
        }
    }

    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Repository.showSnackBar(binding.root,"회원가입이 완료되었습니다.")
                        finish() // 가입창 종료
                    } else {
                        Repository.showSnackBar(binding.root,"이미 존재하는 계정입니다.")
                    }
                }
        }
    }
}