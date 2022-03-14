package com.example.k_health.home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.k_health.*
import com.example.k_health.DBKey.Companion.STORAGE_URL_USERPROFILE
import com.example.k_health.Repository.userId
import com.example.k_health.databinding.FragmentHomeBinding
import com.example.k_health.health.TimeInterface
import com.example.k_health.health.model.HealthRecord
import com.example.k_health.health.model.UserHealthRecord
import com.example.k_health.home.adapter.TodoListAdapter
import com.example.k_health.home.model.TodoList
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import okhttp3.internal.filterList

class HomeFragment : Fragment(R.layout.fragment_home), TimeInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val db = FirebaseFirestore.getInstance()
    private val userInfoDialog: Dialog by lazy { Dialog(requireContext()) }
    private var scope = MainScope()
    private var todolist: ArrayList<TodoList> = arrayListOf()
    private var todoListAdater = TodoListAdapter(todolist)

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        isNicknameNotNull()
        uploadProfileImage()
        notcompletedTodoList(false)
        userInfoSetPopup()
        logoutButton()
        initRecyclerView()
        clickTodoButton()


    }


    private fun setDefaultValues() {
        val defaultValuesData = mutableMapOf<String, Any>()
        defaultValuesData.set("userWeight", "00.0")
        defaultValuesData.set("userheight", "00.0")
        defaultValuesData.set("userFat", "00.0")
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(defaultValuesData)
            .addOnSuccessListener {
                Log.d(TAG, "setDefaultValues")
            }
            .addOnFailureListener {

            }
    }

    private fun isNicknameNotNull() {

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document["userNickname"] != null) {
                    Log.d("Home", "userNickname : ${document["userNickname"]}")
                    setUserProfile()
                    setProgressView()
                } else {
                    setDefaultValues()
                    setUserProfile()
                    showNicknameInputPopup()
                }
            }
            .addOnFailureListener { Error ->
                Log.d("Error", "Error : $Error")
            }
    }

    private fun setUserProfile() {

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                binding.userNameTextView.text = (document["userNickname"].toString()).plus("님")
                binding.userWeightTextView.text = document["userWeight"].toString() ?: "00.0"
                binding.userMuscleTextView.text = document["userMuscle"].toString() ?: "00.0"
                binding.userFatTextView.text = document["userFat"].toString() ?: "00.0"

            }
            .addOnFailureListener {
                Log.d("Error", "error : $it")
            }


        storage.getReferenceFromUrl(STORAGE_URL_USERPROFILE)
            .child("${userId}.png").downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Glide.with(this)
                        .load(it.result)
                        .into(binding.userProfileImageView)
                }
            }
            .addOnFailureListener { error ->
                Glide.with(this)
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .into(binding.userProfileImageView)
            }
    }


    // 팝업창으로 받은 이름을 DB에 저장
    private fun showNicknameInputPopup() {
        val editText = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("닉네임을 입력해주세요 \n8자 이하로 작성이 가능합니다.")
            .setView(editText)
            .setPositiveButton("저장") { _, _ ->
                if (editText.text.isEmpty() || editText.text.length > 8) {
                    Snackbar.make(requireView(), "항목을 다 채워주세요.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", object : View.OnClickListener {
                            override fun onClick(v: View?) {

                            }
                        })
                        .show()
                    showNicknameInputPopup()
                } else {
                    saveUserNickname(editText.text.toString())
                    //setProgressView()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun userInfoSetPopup() {
        binding.userInfoFloatingButton.setOnClickListener {
            showPopup()
        }
    }

    private fun logoutButton() {
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun clickTodoButton() = with(binding) {
        todoLayout.setOnClickListener {
            notcompletedTodoList(false)
        }
        completeLayout.setOnClickListener {
            notcompletedTodoList(true)
        }
    }

    private fun notcompletedTodoList(isDone: Boolean) {
        val today = timeGenerator()
        val mealtimeList = arrayListOf("아침식사", "점심식사", "저녁식사")
        var isCompleted: Boolean
        var eatTime = "12:00 PM"
        val pref = activity?.getSharedPreferences("pref", 0)
        val healthisComplete = pref?.getStringSet(today, mutableSetOf("", ""))!!.toList().isEmpty().not()
        todolist.clear()
        hideAlertText()

        todolist.add(
            TodoList(
                R.drawable.ic_baseline_fitness_center_24_2,
                "운동",
                eatTime,
                healthisComplete
            )
        )

        for (i in mealtimeList.indices) {
            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(userId)
                .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                .document(today) // 오늘의 날짜
                .collection(mealtimeList[i]) // 식사 구분
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                     eatTime = when (mealtimeList[i]) {
                        "아침식사" -> "08:00 AM"
                        "점심식사" -> "13:00 PM"
                        else -> "18:00 PM"
                    }

                    isCompleted = querySnapshot!!.isEmpty.not() // 비어있다면 -> 오늘 할 일
                    todolist.add(
                        TodoList(
                            R.drawable.ic_baseline_restaurant_24_2,
                            mealtimeList[i],
                            eatTime,
                            isCompleted
                        )
                    )
                    Log.d(TAG,"original: $todolist")
                    if (i == mealtimeList.lastIndex) {
                        val originalList = todolist.clone() as ArrayList<TodoList>
                        todolist.removeIf { it.isComplete == isDone.not() }
                        Log.d(TAG,"new: $todolist")
                        todoListAdater.notifyDataSetChanged()
                        binding.todoTaskTextView.text = originalList.filter { it.isComplete == false }.size.toString()
                        binding.completeTaskTextView.text = originalList.filter { it.isComplete == true }.size.toString()
                        // 오늘 할 일의 리스트가 비워져있다면 임무 완료 팝업 띄우기
                        if (isDone == false && todolist.isEmpty()) {
                            showAlertText()
                        }
                    }
                }
        }

    }

    private fun initRecyclerView() {
        binding.todolistRecyclerView.apply {
            adapter = todoListAdater
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showPopup() {
        userInfoDialog.apply {
            setContentView(R.layout.userinfo_dialog)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            show()
        }
        val userWeightEditText = userInfoDialog.findViewById<EditText>(R.id.user_weight_EditText)
        val userMuscleEditText = userInfoDialog.findViewById<EditText>(R.id.user_muscle_EditText)
        val userFatEditText = userInfoDialog.findViewById<EditText>(R.id.user_fat_EditText)

        userInfoDialog.findViewById<Button>(R.id.submitButton).setOnClickListener { view ->

            if (userWeightEditText.text.isEmpty() || userMuscleEditText.text.isEmpty() || userFatEditText.text.isEmpty()) {
                Snackbar.make(view, "항목을 다 채워주세요.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", object : View.OnClickListener {
                        override fun onClick(v: View?) {

                        }
                    })
                    .show()
            } else {
                val userData = mutableMapOf<String, Any>(
                    "userWeight" to userWeightEditText.text.toString(),
                    "userMuscle" to userMuscleEditText.text.toString(),
                    "userFat" to userFatEditText.text.toString()
                )

                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(userId)
                    .update(userData)
                    .addOnSuccessListener {
                        userInfoDialog.dismiss()
                        Snackbar.make(requireView(), "신체정보가 등록되었습니다.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", object : View.OnClickListener {
                                override fun onClick(v: View?) {

                                }
                            })
                            .show()
                        setUserProfile()
                        setProgressView()
                    }
                    .addOnFailureListener {

                    }
            }

        }

    }

    private fun setProgressView() = with(binding) {

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                weightProgressView.labelText =
                    getString(R.string.weight).plus(": " + document["userWeight"].toString() + "kg")
                weightProgressView.progress = document["userWeight"].toString().toFloat()

                // 골격근량 최대치 -> 이 반이 평균
                if (document["userWeight"].toString().toFloat() != 00.0f) {
                    muscleProgressView.max = document["userWeight"].toString().toFloat() * 0.48f * 2
                }

                muscleProgressView.labelText =
                    getString(R.string.Skeletal_Muscle_Mass).plus(": " + document["userMuscle"].toString() + "kg")
                muscleProgressView.progress = document["userMuscle"].toString().toFloat()

                fatProgressView.labelText =
                    getString(R.string.body_Fat_Percentage).plus(": " + document["userFat"].toString() + "%")
                fatProgressView.progress = document["userFat"].toString().toFloat()
            }
            .addOnFailureListener {
                Log.d("Error", "error : $it")
            }
    }

    // DB에 닉네임을 저장
    private fun saveUserNickname(nickname: String) {
        val userNickname = mutableMapOf<String, Any>()
        userNickname["userNickname"] = nickname
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(userNickname)
            .addOnSuccessListener {
                Snackbar.make(requireView(), "닉네임이 등록되었습니다.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", object : View.OnClickListener {
                        override fun onClick(v: View?) {

                        }
                    })
                    .show()
                updateUserNickname()
            }
            .addOnFailureListener {

            }
    }

    private fun updateUserNickname() {
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                binding?.userNameTextView?.text = (document["userNickname"].toString()).plus("님")

            }
            .addOnFailureListener {
            }
    }

    /*// 기존 등록한 유저 프로필 가져오기
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
                Glide.with(requireContext())
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .into(binding!!.userProfileImageView)
            }
    }*/

    private fun uploadProfileImage() {
        binding.userProfileImageView.setOnClickListener {
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
                        hideProgress()
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

    private fun showAlertText() = with(binding){
        alertLinearLayout.isVisible = true
    }

    private fun hideAlertText() = with(binding){
        alertLinearLayout.isVisible = false
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }

    override fun timeGenerator(): String {
        return super.timeGenerator()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
        scope.cancel()
    }
}