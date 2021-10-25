package com.example.k_health

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.k_health.DBKey.Companion.COLLECTION_NAME_USERS
import com.example.k_health.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Authentication 관리 클래스
        auth = Firebase.auth

        // Facebook 로그인 처리 결과 관리 클래스
        callbackManager = CallbackManager.Factory.create()


        initFacebookLoginButton()
    }
    private fun initFacebookLoginButton() {
        binding.facebookLoginButton.setPermissions("email","public_profile")
        binding.facebookLoginButton.registerCallback(callbackManager, object:
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // 로그인이 성공됐을때
                // token 넘겨주기
                handleFacebookAccessToken(result.accessToken)

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onCancel() { }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, "로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    successLogin()
                } else {
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun successLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid.orEmpty()
        // 현재 유저의 아이디를 가져온다
        val user = mutableMapOf<String,Any>()
        user["userId"] = userId
        db.collection(COLLECTION_NAME_USERS)
            .document(userId)
            .set(user)
            .addOnCompleteListener {
                // 성공할 경우
                Toast.makeText(this, "유저의 정보가 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { errorMessage ->
                Log.d(TAG,"Error: $errorMessage")
            }

        finish()
    }

    fun moveMainPage(user: FirebaseUser?) {

        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        // 자동 로그인 설정
        // moveMainPage(auth.currentUser)
    }
}