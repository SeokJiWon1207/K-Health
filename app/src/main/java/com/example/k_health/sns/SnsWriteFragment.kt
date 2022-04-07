package com.example.k_health.sns

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentSnsWriteBinding
import com.example.k_health.health.TimeInterface
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SnsWriteFragment: Fragment(R.layout.fragment_sns_write), TimeInterface {

    companion object {
        const val TAG = "SnsWriteFragment"
    }
    private var _binding: FragmentSnsWriteBinding? = null
    private val binding get() = _binding!!
    private var selectedUri: Uri? = null
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val db = FirebaseFirestore.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSnsWriteBinding.bind(view)

        setToolbar()
        getLoadGalleryImage()


    }

    private fun setToolbar() = with(binding) {
        val snsFragment = SnsFragment()

        toolbar.inflateMenu(R.menu.toolbar_sns_write)

        // 프래그먼트가 메뉴 관련 콜백을 수신하려 한다고 시스템에 알림
        // 메뉴 관련 이벤트(생성, 클릭 등)가 발생하면 이벤트 처리 메서드가 먼저 활동에서 호출된 후 프래그먼트에서 호출
        setHasOptionsMenu(true)
        toolbar.title = "글쓰기"

        // 액션버튼 클릭 했을 때 이벤트 처리
        toolbar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_home -> {
                    // 홈버튼 눌렀을 때
                    (activity as MainActivity).replaceFragment(snsFragment)
                    Log.d(SnsFragment.TAG, "홈 버튼 클릭")
                    true
                }
                R.id.action_upload -> {
                    // 체크버튼을 눌렀을 때
                    Log.d(SnsFragment.TAG, "글쓰기 완료")
                    uploadPhoto(selectedUri!!, successHandler = { uri ->
                        Snackbar.make(requireView(), "글이 작성 되었습니다.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", object : View.OnClickListener {
                                override fun onClick(v: View?) {

                                }
                            })
                            .show()
                    }, errorHandler = {
                            Snackbar.make(requireView(), "글 작성에 실패했습니다.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("확인", object : View.OnClickListener {
                                    override fun onClick(v: View?) {

                                    }
                                })
                                .show()
                        })
                    true
                }
                else -> false
            }
        }
    }

    // 1-1) 업로드할 사진 불러오기
    private fun getLoadGalleryImage() = with(binding) {
        addPhotoFab.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                    Log.d(TAG,"권한 허락")
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                    Log.d(TAG,"권한 없음")
                }
            }
        }
    }



    // storage에 업로드
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        // 현재의 시간명으로 파일이름 지정
        val fileName = "${timeGenerator()}.png"
        storage.reference.child("sns/${userId}").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("sns/${userId}").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                            // uploadDB(uri)
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    // fireStore에 저장
    private fun uploadDB(photoUri: Uri) {
        val userProfile = mutableMapOf<String, Any>()
        userProfile["userProfile"] = photoUri.toString()

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(userProfile)
            .addOnSuccessListener {
                Snackbar.make(requireView(), "프로필 사진이 등록되었습니다", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", object : View.OnClickListener {
                        override fun onClick(v: View?) {

                        }
                    })
                    .show()
            }
            .addOnFailureListener {

            }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)

    }

    // 권한 팝업 창창
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data

                // 선택한 사진이 있을 경우
                if (uri != null) {
                    Glide.with(binding.uploadPhotoImageView.context)
                        .load(uri)
                        .into(binding.uploadPhotoImageView)

                    selectedUri = uri // 선택한 사진을 selectedUri에 저장

                    Log.d(TAG,"선택한 사진이 존재")
                } else {
                    Snackbar.make(requireView(), "사진을 가져오지 못했습니다", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", object : View.OnClickListener {
                            override fun onClick(v: View?) {

                            }
                        })
                        .show()
                }
            }
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDateTime.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd/HHmmss"))

        return todayNow
    }


}