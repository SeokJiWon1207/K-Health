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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.k_health.*
import com.example.k_health.databinding.FragmentHomeBinding
import com.example.k_health.food.FoodSearchFragment
import com.example.k_health.health.HealthFragment
import com.example.k_health.health.TimeInterface
import com.example.k_health.home.adapter.TodoListAdapter
import com.example.k_health.home.model.TodoList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(R.layout.fragment_home), TimeInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedUri: Uri? = null
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val db = FirebaseFirestore.getInstance()
    private val userInfoDialog: Dialog by lazy { Dialog(requireContext()) }
    private var scope = MainScope()
    private val today = timeGenerator()
    private val bundle = Bundle()

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        isNicknameNotNull()
        uploadProfileImage()
        userInfoSetPopup()
        notcompletedTodoList(false)
        clickTodoButton()


    }

    private fun setDefaultValues() {
        Log.d(TAG,"??????")
        val defaultValuesData = mutableMapOf<String, Any>()
        defaultValuesData.set("userWeight", "00.0")
        defaultValuesData.set("userheight", "00.0")
        defaultValuesData.set("userMuscle", "00.0")
        defaultValuesData.set("userFat", "00.0")
        defaultValuesData.set("userActivityLevel", "0")
        defaultValuesData.set("userRecommendedKcal", "0")
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .set(defaultValuesData)
            .addOnSuccessListener {
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
                    setUserProfile()
                    setProgressView()
                } else {
                    // ????????????
                    setDefaultValues()
                    setUserProfile()
                    showNicknameInputPopup()
                }
            }
            .addOnFailureListener { _ ->
            }
    }

    private fun setUserProfile() = with(binding) {

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                userNameTextView.text = if(document["userNickname"]==null) "K-Health" else (document["userNickname"].toString()).plus("???")
                userWeightTextView.text = document["userWeight"].toString()
                userMuscleTextView.text = document["userMuscle"].toString()
                userFatTextView.text = document["userFat"].toString()

                Glide.with(userProfileImageView.context)
                    .load(document["userProfile"].toString())
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24) // load??? ????????? ????????? ?????? ?????????
                    .fallback(R.drawable.ic_baseline_account_circle_24) // load??? url??? null??? ?????? ?????? ?????????
                    .into(userProfileImageView)

            }
            .addOnFailureListener {
            }


        /*storage.getReferenceFromUrl(STORAGE_URL_USERPROFILE)
            .child("${userId}.png").downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Glide.with(binding.userProfileImageView.context)
                        .load(it.result)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .into(binding.userProfileImageView)
                }
            }
            .addOnFailureListener { error ->

            }*/
    }


    // ??????????????? ?????? ????????? DB??? ??????
    private fun showNicknameInputPopup() {
        val editText = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("???????????? ?????????????????? \n8??? ????????? ????????? ???????????????.")
            .setView(editText)
            .setPositiveButton("??????") { _, _ ->
                if (editText.text.isEmpty() || editText.text.length > 8) {
                    Repository.showSnackBar(requireView(),"????????? ??? ???????????????.")
                    showNicknameInputPopup()
                } else {
                    saveUserNickname(editText.text.toString())
                    setProgressView()
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

    private fun clickTodoButton() = with(binding) {
        todoLayout.setOnClickListener {
            notcompletedTodoList(false)
        }
        completeLayout.setOnClickListener {
            notcompletedTodoList(true)
        }
    }

    private fun notcompletedTodoList(isDone: Boolean) = with(binding) {
        var todolist = arrayListOf<TodoList>()
        todolist.clear()
        hideAlertText()
        hideCongratulationView()
        CoroutineScope(Dispatchers.IO).launch {

            runBlocking {
                todolist = getResultTodayMealtimeList()
            }

            scope.launch {
                val todoListAdater = TodoListAdapter(todolist)
                val originalList = todolist.clone() as ArrayList<TodoList>
                todolist.removeIf { it.isComplete == !isDone }
                initRecyclerView(todoListAdater)
                clickAdapter(todoListAdater)
                todoTaskTextView.text =
                    originalList.filter { it.isComplete == false }.size.toString()
                completeTaskTextView.text =
                    originalList.filter { it.isComplete == true }.size.toString()

                // ?????? ??? ?????? ???????????? ?????? ?????? ?????? ?????????
                if (isDone == false && todolist.isEmpty()) {
                    showCongratulationView()
                    showAlertText()
                }
            }
        }
    }

    private suspend fun getResultTodayMealtimeList(): ArrayList<TodoList> {
        val mealtimeList = arrayListOf("????????????", "????????????", "????????????")
        val todolist = arrayListOf<TodoList>()
        var isCompleted: Boolean
        var eatTime = "12:00 PM"
        val healthSet = GlobalApplication.prefs.getStringSet(today, mutableSetOf("", ""))

        healthSet.remove("") // ????????? ""??? ?????? ????????? ???????????????
        val isHealthComplete = healthSet.toMutableList().isEmpty().not() // ??????????????? false -> ????????? ?????? ??????

        Log.d(TAG,"pref: ${GlobalApplication.prefs.getAll()}")

        todolist.add(
            TodoList(
                R.drawable.ic_baseline_fitness_center_24_2,
                "??????",
                eatTime,
                isHealthComplete
            )
        )
        return try {
            for (i in mealtimeList.indices) {
                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(userId)
                    .collection(DBKey.COLLECTION_NAME_FOODRECORD) // ??????????????????
                    .document(today) // ????????? ??????
                    .collection(mealtimeList[i]) // ?????? ??????
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        eatTime = when (mealtimeList[i]) {
                            "????????????" -> "08:00 AM"
                            "????????????" -> "13:00 PM"
                            else -> "18:00 PM"
                        }
                        isCompleted = querySnapshot!!.isEmpty.not() // ??????????????? -> ?????? ??? ???
                        todolist.add(
                            TodoList(
                                R.drawable.ic_baseline_restaurant_24_2,
                                mealtimeList[i],
                                eatTime,
                                isCompleted
                            )
                        )
                    }.await()
            }
            todolist
        } catch (e: FirebaseFirestoreException) {
            todolist
        }
    }

    private fun initRecyclerView(todoListAdapter: TodoListAdapter) = with(binding) {
        todolistRecyclerView.apply {
            adapter = todoListAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
                Repository.showSnackBar(requireView(), "????????? ??? ???????????????.")
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
                        Repository.showSnackBar(requireView(), "??????????????? ?????????????????????.")
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
                    getString(R.string.weight).plus(
                        ": " + document["userWeight"].toString().plus("kg")
                    )
                weightProgressView.progress = document["userWeight"].toString().toFloat()

                // ???????????? ????????? -> ??? ?????? ??????
                if (document["userWeight"].toString().toFloat() != 00.0f) {
                    muscleProgressView.max = document["userWeight"].toString().toFloat() * 0.48f * 2
                }

                if (document["userMuscle"].toString().toFloat() < 15) muscleProgressView.max = 45f
                Log.d(TAG,"max: ${muscleProgressView.max}")
                muscleProgressView.labelText =
                    getString(R.string.Skeletal_Muscle_Mass).plus(
                        ": " + document["userMuscle"].toString().plus("kg")
                    )
                muscleProgressView.progress = document["userMuscle"].toString().toFloat()
                Log.d(TAG,"progress: ${muscleProgressView.progress}")
                fatProgressView.labelText =
                    getString(R.string.body_Fat_Percentage).plus(
                        ": " + document["userFat"].toString().plus("%")
                    )
                fatProgressView.progress = document["userFat"].toString().toFloat()

                Log.d(TAG,"max: ${fatProgressView.max}")
                Log.d(TAG,"fat progress: ${fatProgressView.progress}")
            }
            .addOnFailureListener {
            }
    }

    // DB??? ???????????? ??????
    private fun saveUserNickname(nickname: String) {
        val userNickname = mutableMapOf<String, Any>()
        userNickname["userNickname"] = nickname
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .update(userNickname)
            .addOnSuccessListener {
                Repository.showSnackBar(requireView(), "???????????? ?????????????????????.")
                updateUserNickname()
            }
            .addOnFailureListener {

            }
    }

    private fun updateUserNickname() = with(binding) {
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                userNameTextView.text = (document["userNickname"].toString()).plus("???")
            }
            .addOnFailureListener {
            }
    }


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

    // storage??? ?????????
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
                Repository.showSnackBar(requireView(), "????????? ????????? ?????????????????????.")
            }
            .addOnFailureListener {

            }
        setUserProfile()
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
                    Repository.showSnackBar(requireView(), "????????? ?????????????????????.")
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
                    uploadPhoto(selectedUri!!, successHandler = { _ -> Repository.showSnackBar(requireView(), "?????? ???????????? ??????????????????.")
                        hideProgress()
                    },
                        errorHandler = {
                            Repository.showSnackBar(requireView(), "?????? ???????????? ??????????????????.")
                            hideProgress()
                        })
                } else {
                    Repository.showSnackBar(requireView(), "????????? ???????????? ???????????????.")
                }

            }
            else -> {
                Repository.showSnackBar(requireView(), "????????? ???????????? ???????????????.")
            }
        }
    }

    // ?????? ?????? ??????
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("????????? ???????????????.")
            .setMessage("????????? ???????????? ?????? ???????????????.")
            .setPositiveButton("??????") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()

    }

    private fun clickAdapter(todoListAdapter: TodoListAdapter) {
        todoListAdapter.setOnItemClickListener(object : TodoListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: TodoList, pos: Int) {
                val healthFragment = HealthFragment()
                val foodSearchFragment = FoodSearchFragment()

                val mealTimePosition = when (data.todoName) {
                    "????????????" -> 0
                    "????????????" -> 1
                    "????????????" -> 2
                    else -> 3
                }

                bundle.putInt("selectedMealtime", mealTimePosition)
                foodSearchFragment.arguments = bundle

                data.todoName.apply {
                    if (this.equals("????????????") || this.equals("????????????") || this.equals("????????????")) {
                            (activity as MainActivity).replaceFragment(foodSearchFragment)
                    } else {
                        (activity as MainActivity).replaceFragment(healthFragment)
                    }

                }

            }

        })
    }

    private fun showCongratulationView() = with(binding) {
        val custeomParty = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def), // ???????????? ????????? ???
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100), // ??? ??? ????????? ??????????????? ????????? ????????? ???
            position = Position.Relative(0.5, 0.3)
        )
        congratulationView.apply {
            visibility = View.VISIBLE
            start(custeomParty)
        }
    }

    private fun hideCongratulationView() = with(binding) {
        congratulationView.visibility = View.GONE
    }

    private fun showAlertText() = with(binding) {
        alertLinearLayout.isVisible = true
    }

    private fun hideAlertText() = with(binding) {
        alertLinearLayout.isVisible = false
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // _binding = null
        scope.cancel()
    }
}