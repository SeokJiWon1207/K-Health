package com.example.k_health

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.k_health.DBKey.Companion.COLLECTION_NAME_USERS
import com.example.k_health.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.*
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.nhn.android.naverlogin.OAuthLogin
import com.royrodriguez.transitionbutton.TransitionButton
import com.royrodriguez.transitionbutton.TransitionButton.OnAnimationStopEndListener


class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private lateinit var callbackManager: CallbackManager
    private lateinit var mOAuthLoginInstance: OAuthLogin
    private var googleSignInClient: GoogleSignInClient? = null
    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "LoginActivity"
        const val GOOGLE_LOGIN_CODE = 9001 // Intent Request ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        //구글 로그인 옵션
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_webclient_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Facebook 로그인 처리 결과 관리 클래스
        callbackManager = CallbackManager.Factory.create()

        // 네이버 로그인
        /*mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_password),
            getString(R.string.naver_client_name)
        )*/
        initSignInWithEmailAndPassword()
        initRegisterButton()
        // initNaverLoginButton()
        // initKakaoLoginButton()
        initFacebookLoginButton()
        initGoogleLoginButton()
    }

    private fun initSignInWithEmailAndPassword() = with(binding){
        loginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Start the loading animation when the user tap the button
                loginButton.startAnimation()

                // Do your networking task or background work here.
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(Runnable {
                    if (emailEdittext.text!!.isNotEmpty() && passwordEdittext.text!!.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(emailEdittext.text.toString().trim(), passwordEdittext.text.toString().trim())
                            .addOnCompleteListener() { task ->
                                if (task.isSuccessful) {
                                    loginButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                        object: OnAnimationStopEndListener {
                                            override fun onAnimationStopEnd() {
                                                moveMainPage(auth.currentUser)
                                            }
                                        })
                                } else {
                                    // 로그인에 실패했을 때, 옆으로 흔들리는 효과
                                    loginButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                                }
                            }
                    } else {
                        loginButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                    }
                }, 2000)
            }
        })
    }


    private fun initRegisterButton() = with(binding) {
        registerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // Start the loading animation when the user tap the button
                registerButton.startAnimation()

                // Do your networking task or background work here.
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(Runnable {
                    val isSuccessful = true

                    // 로그인에 성공했을 때
                    if (isSuccessful) {
                        registerButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                            object: OnAnimationStopEndListener {
                                override fun onAnimationStopEnd() {
                                    startRegisterActivity()
                                }
                            })
                    }

                }, 1000)
            }
        })
    }

    private fun initFacebookLoginButton() {
        binding.facebookLoginButton.setPermissions("email", "public_profile")
        binding.facebookLoginButton.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // 로그인이 성공됐을때
                Log.d(TAG,"페북 로그인 성공")
                // token 넘겨주기
                handleFacebookAccessToken(result.accessToken)
                // setUidFireStore()
                startMainActivity()
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, "로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initGoogleLoginButton() {
        binding.googleLoginButton.setOnClickListener { googleLogin() }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG,"userId: $userId")
                } else {
                    Log.d(TAG,"task: ${task.exception}")
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
        Log.d(TAG,"signInIntent :$signInIntent")
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    setUidFireStore()
                    moveMainPage(auth.currentUser)
                } else {
                    // 틀렸을 때
                        Log.d(TAG,"Firebase Auth 등록 실패")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        // 구글에서 승인된 정보를 가지고 오기
        if (requestCode == GOOGLE_LOGIN_CODE) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // 이곳이 반환됨
                Repository.showSnackBar(binding.root,"구글 로그인 실패")
            }
        } else {
            Log.d(TAG,"request failed")
        }
    }

    private fun setUidFireStore() {
        auth.currentUser?.let {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"facebook 로그인 실패")
            return
        }

        val user = mutableMapOf<String, Any>()

        user.put("userWeight","00.0")
        user.put("userMuscle","00.0")
        user.put("userFat","00.0")

        db.collection(COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                document["userId"]?.let {
                    user["userId"] = userId
                    db.collection(COLLECTION_NAME_USERS)
                        .document(userId)
                        .set(user)
                        .addOnCompleteListener {
                            Log.d(TAG, "유저가 등록되었습니다")
                        }
                        .addOnFailureListener { errorMessage ->
                            Log.d(TAG, "Error: $errorMessage")
                        }
                }
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG,"$it")
            }
    }


    private fun startMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    private fun startLoginActivity() {
        startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
    }

    private fun startRegisterActivity() {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
    }

    fun moveMainPage(user: FirebaseUser?) {

        if (user != null) {
            startMainActivity()
        } else {
            startLoginActivity()
        }
    }

    /*private fun initNaverLoginButton() {
        val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak")
        object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    startMainActivity()
                } else {
                    val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                    val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

                    Toast.makeText(
                        baseContext, "errorCode:" + errorCode
                                + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.naverLoginButton.setOAuthLoginHandler(mOAuthLoginHandler)
    }*/


    /*private fun initKakaoLoginButton() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Log.d(TAG, "etc error : $error")
                    }
                }
            } else if (token != null) {
                startMainActivity()
            }
        }

        binding.kakaoLoginButton.setOnClickListener {
            // 로그인 공통 callback 구성

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            UserApiClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }
    }*/

}