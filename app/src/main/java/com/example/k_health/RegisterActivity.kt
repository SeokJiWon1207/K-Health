package com.example.k_health

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.k_health.databinding.ActivityRegisterBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val TAG = "isCorrect"
    }

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        checkEmailAndPassword()
        initRegisterButton()

    }

    private fun initToolbar() = with(binding) {
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setTitle("회원가입")
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
        }
    }

    private fun initRegisterButton() = with(binding) {
        registerButton.setOnClickListener {
            createAccount(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
    }

    private fun checkEmailAndPassword() = with(binding) {
        var currentPassword = ""

        passwordEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0!!.length < 6 || p0.length > 20) showAlertPassword() else hideAlertPassword()
                }

                override fun afterTextChanged(p0: Editable?) {
                    currentPassword = p0.toString()
                }
            })
        }

        passwordCheckEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    if (p0!!.length >= 6 && currentPassword.equals(p0.toString())) {
                        // 6자리 이상이며 비밀번호와 맞을 때
                        correctAlertPasswordCheck()
                        setClickOnRegisterButton()
                    } else if (p0.length >= 6 && !currentPassword.equals(p0.toString())) {
                        // 6자리 이상이며 비밀번호와 틀릴 대
                        notCorrectAlertPasswordCheck()
                        setClickOffRegisterButton()
                    } else {
                        hideAlertPasswordCheck()
                        setClickOffRegisterButton()
                    }
                }
            })
        }
    }

    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Repository.showSnackBar(binding.root, "회원가입이 완료되었습니다.")
                    } else {
                        Repository.showSnackBar(binding.root, "이미 존재하는 계정이거나 \n이메일 형식이 올바르지 않습니다.")
                    }
                }
        }
    }

    private fun showAlertPassword() = with(binding) {
        alertPasswordTextView.visibility = View.VISIBLE
    }

    private fun hideAlertPassword() = with(binding) {
        alertPasswordTextView.visibility = View.GONE
    }

    private fun hideAlertPasswordCheck() = with(binding) {
        alertPasswordCheckTextView.visibility = View.GONE
    }

    private fun notCorrectAlertPasswordCheck() = with(binding) {
        alertPasswordCheckTextView.apply {
            visibility = View.VISIBLE
            text = "비밀번호와 일치하지 않습니다."
            setTextColor(getColor(R.color.red))
        }
    }

    private fun correctAlertPasswordCheck() = with(binding) {
        alertPasswordCheckTextView.apply {
            visibility = View.VISIBLE
            text = "비밀번호와 일치합니다."
            setTextColor(getColor(R.color.blue))
        }
    }

    private fun setClickOnRegisterButton() = with(binding) {
        registerButton.isEnabled = true
    }

    private fun setClickOffRegisterButton() = with(binding) {
        registerButton.isEnabled = false
    }
}