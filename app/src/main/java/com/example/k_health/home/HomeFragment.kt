package com.example.k_health.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.k_health.DBKey
import com.example.k_health.DBKey.Companion.STORAGE_URL_USERPROFILE
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid.orEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        getProfileImage()
        uploadProfileImage()

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document("userNickname")
            .get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d("Home", "DocumentSnapshot data: ${document.data}")
                } else {
                    showNicknameInputPopup()
                }
            }
            .addOnFailureListener { Error ->
                Log.d("Error", "Error : $Error")
            }

    }

    // 팝업창으로 받은 이름을 DB에 저장
    private fun showNicknameInputPopup() {
        val editText = EditText(requireContext())

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("닉네임을 입력해주세요 \n 8자 이하로 작성이 가능합니다.")
            .setView(editText)
            .setPositiveButton("저장") { _, _ ->
                if (editText.text.isEmpty() || editText.text.length >= 8) {
                    Toast.makeText(requireContext(), "다시 입력해주세요", Toast.LENGTH_SHORT).show()
                    showNicknameInputPopup()
                } else {
                    saveUserNickname(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    // DB에 닉네임을 저장
    private fun saveUserNickname(nickname: String) {
        val userNickname = mutableMapOf<String, Any>()
        userNickname["userNickname"] = nickname
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(userNickname)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "닉네임이 등록되었습니다", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {

            }
    }

    // 기존 등록한 유저 프로필 가져오기
    private fun getProfileImage() {
        storage.getReferenceFromUrl(STORAGE_URL_USERPROFILE)
            .child("${userId}.png").downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(requireContext())
                    .load(it.result)
                    .into(binding!!.userProfileImageView)
            }
        }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: $error ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage() {
        binding?.userProfileImageView?.setOnClickListener {
            showProgress()
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )


                }
            }
        }
    }

    // storage에 업로드
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${userId}.png"
        storage.reference.child("userprofile").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("userprofile").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                            uploadDB(uri)
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadDB(photoUri: Uri) {
        val userProfile = mutableMapOf<String, Any>()
        userProfile["userProfile"] = photoUri.toString()

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(userProfile)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "프로필 사진이 등록됐습니다", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {

            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(requireContext(), "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    binding?.userProfileImageView?.setImageURI(uri)
                    selectedUri = uri
                    uploadPhoto(selectedUri!!, successHandler = { uri ->
                        Toast.makeText(requireContext(), "사진 업로드에 성공했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    },
                        errorHandler = {
                            Toast.makeText(requireContext(), "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT)
                                .show()
                            hideProgress()
                        })
                } else {
                    Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            else -> {
                Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun showProgress() {
        binding?.progressBar?.isVisible = true
    }

    private fun hideProgress() {
        binding?.progressBar?.isVisible = false
    }
}