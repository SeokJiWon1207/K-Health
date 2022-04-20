package com.example.k_health.food

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.*
import com.example.k_health.databinding.FragmentFoodBinding
import com.example.k_health.databinding.LayoutFoodBinding
import com.example.k_health.food.adapter.FoodRecordListAdapter
import com.example.k_health.food.data.models.Item
import com.example.k_health.health.TimeInterface
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodFragment : Fragment(R.layout.fragment_food), TimeInterface {

    companion object {
        const val TAG = "FoodFragment"
    }

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private val db = FirebaseFirestore.getInstance()
    private val foodSearchFragment = FoodSearchFragment()
    private val foodInfoFragment = FoodInfoFragment()
    private val breakfastFoodList: ArrayList<Item> = arrayListOf()
    private val lunchFoodList: ArrayList<Item> = arrayListOf()
    private val dinnerFoodList: ArrayList<Item> = arrayListOf()
    private val etcFoodList: ArrayList<Item> = arrayListOf()
    private val totalFoodList: ArrayList<Item> = arrayListOf()
    private lateinit var breakfastFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var lunchFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var dinnerFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var etcFoodRecordListAdapter: FoodRecordListAdapter
    private val activityLevelInfoDialog: Dialog by lazy { Dialog(requireContext()) }
    private val bundle = Bundle()
    private val todayNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)
        isActivityLevelNotNull()
        totalFoodList.clear()
        setSelectedDateDefaultValues()
        initViews()
        getUserFoodRecord(todayNow, FoodTime.BREAKFAST.time, breakfastFoodList, breakfastFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.LUNCH.time, lunchFoodList, lunchFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.DINNER.time, dinnerFoodList, dinnerFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.ETC.time, etcFoodList, etcFoodRecordListAdapter)

        getFoodRecordWithCalendar()

    }

    // 음식을 삭제 할 때 날짜의 기본값을 오늘로 설정해 줌
    private fun setSelectedDateDefaultValues() {
        GlobalApplication.prefs.setString("selectedDate", todayNow)
    }

    @SuppressLint("ResourceAsColor")
    private fun initViews() = with(binding) {
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            breakfastFoodRecordListAdapter =
                FoodRecordListAdapter(breakfastFoodList, onClickDeleteButton = {
                    //3. onBindViewHolder에서 listposition을 전달받고 이 함수를 실행하게 된다.
                    // deleteRecord 함수가 포지션값인 it을 받고 지운다.
                    deleteRecord(
                        it,
                        breakfastFoodList,
                        this.foodRecordRecyclerView,
                        FoodTime.BREAKFAST.time
                    )
                })
            initRecyclerView(foodRecordRecyclerView, breakfastFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            lunchFoodRecordListAdapter =
                FoodRecordListAdapter(lunchFoodList, onClickDeleteButton = {
                    deleteRecord(
                        it,
                        lunchFoodList,
                        this.foodRecordRecyclerView,
                        FoodTime.LUNCH.time
                    )
                })
            initRecyclerView(foodRecordRecyclerView, lunchFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            dinnerFoodRecordListAdapter =
                FoodRecordListAdapter(dinnerFoodList, onClickDeleteButton = {
                    deleteRecord(
                        it,
                        dinnerFoodList,
                        this.foodRecordRecyclerView,
                        FoodTime.DINNER.time
                    )
                })
            initRecyclerView(foodRecordRecyclerView, dinnerFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            etcFoodRecordListAdapter = FoodRecordListAdapter(etcFoodList, onClickDeleteButton = {
                deleteRecord(it, etcFoodList, this.foodRecordRecyclerView, FoodTime.ETC.time)
            })
            initRecyclerView(foodRecordRecyclerView, etcFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }

    private fun deleteRecord(
        item: Item,
        foodlist: ArrayList<Item>,
        recyclerView: RecyclerView,
        mealtime: String
    ) {
        foodlist.remove(item)
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val toremovedDate = GlobalApplication.prefs.getString("selectedDate", today)
        val toremovedFoodname = item.foodName

        Log.d(TAG,"day: $toremovedDate")

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(toremovedDate!!) // 캘린더 선택 날짜
            .collection(mealtime) // 식사 구분
            .document(toremovedFoodname!!)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "removed item")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "removed failed : $e")
            }

        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getFoodRecordWithCalendar() {
        showProgress()
        binding.foodCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String =
                if (month > 10) "${month + 1}" else String.format("%02d", month + 1)
            val dayOfMonthString: String =
                if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val todayDate = "${year}/${monthString}/${dayOfMonthString}" //
            val selectedDate = "${year}${monthString}${dayOfMonthString}" // firestore의 path로 '/'사용불가

            GlobalApplication.prefs.setString("selectedDate", selectedDate)

            bundle.putString("todayDate", todayDate)
            bundle.putString("selectedDate", selectedDate)

            foodSearchFragment.arguments = bundle

            getUserFoodRecord(selectedDate, FoodTime.BREAKFAST.time, breakfastFoodList, breakfastFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.LUNCH.time, lunchFoodList, lunchFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.DINNER.time, dinnerFoodList, dinnerFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.ETC.time, etcFoodList, etcFoodRecordListAdapter)

            setProgressView()
            totalFoodList.clear()
        }
        hideProgress()
    }

    private fun getUserFoodRecord(
        selectedDate: String,
        mealtime: String,
        foodlist: ArrayList<Item>,
        foodRecordListAdapter: FoodRecordListAdapter
    ) {
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(selectedDate) // 캘린더 선택 날짜
            .collection(mealtime) // 식사 구분
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                foodlist.clear()
                for (snapshot in querySnapshot!!.documents) {
                    val foodRecordItem = snapshot.toObject(Item::class.java)
                    foodlist.add(foodRecordItem!!)
                    totalFoodList.add(foodRecordItem!!)
                }
                setupUserKcalInfo()
                setProgressView()
                foodRecordListAdapter.notifyDataSetChanged()
            }
    }

    private fun initRecyclerView(
        recyclerView: RecyclerView,
        foodRecordListAdapter: FoodRecordListAdapter,
    ) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodRecordListAdapter
        }
    }


    // 활동지수 유무확인
    private fun isActivityLevelNotNull() {
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document["userActivityLevel"] != null && document["userActivityLevel"] != "0") {
                    setupUserKcalInfo()
                    setProgressView()
                } else {
                    // setDefaultValues()
                    showActivityLevelInputPopup()
                }
            }
            .addOnFailureListener { Error ->
                Log.d("Error", "Error : $Error")
            }
    }

    private fun showActivityLevelInputPopup() {
        activityLevelInfoDialog.apply {
            setContentView(R.layout.activitylevelinfo_dialog)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            show()
        }

        val userAgeEditText = activityLevelInfoDialog.findViewById<EditText>(R.id.user_age_EditText)
        val userWeightEditText =
            activityLevelInfoDialog.findViewById<EditText>(R.id.user_weight_EditText)
        val userheightEditText =
            activityLevelInfoDialog.findViewById<EditText>(R.id.user_height_EditText)
        val userSexRadioGroup =
            activityLevelInfoDialog.findViewById<RadioGroup>(R.id.sex_RadioGroup)
        val man = activityLevelInfoDialog.findViewById<RadioButton>(R.id.sex_man)
        val woman = activityLevelInfoDialog.findViewById<RadioButton>(R.id.sex_woman)
        val userActivityLevelRadioGroup =
            activityLevelInfoDialog.findViewById<RadioGroup>(R.id.activity_Level_RadioGroup)
        val submitButton = activityLevelInfoDialog.findViewById<Button>(R.id.submitButton)

        var userSex: String? = null // 유저 성별
        var userActivityLevel = 0 // 유저 활동지수

        userSexRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.sex_man -> userSex = man.text.toString()
                else -> userSex = woman.text.toString()
            }
        }

        userActivityLevelRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.activity_Level_1 -> userActivityLevel = 25 // 활동지수 수치
                R.id.activity_Level_2 -> userActivityLevel = 30
                R.id.activity_Level_3 -> userActivityLevel = 35
                else -> userActivityLevel = 40
            }
        }

        submitButton.setOnClickListener { _ ->
            if (userheightEditText.text.isEmpty() || userAgeEditText.text.isEmpty() || userWeightEditText.text.isEmpty()
                || userActivityLevel.equals(0) || userSex.equals(null)
            ) {
                Repository.showSnackBar(
                    requireView(),
                    "항목을 다 채워주세요"
                )
                return@setOnClickListener
            } else {
                val userHeight = userheightEditText.text.toString().toDouble()
                // 일일 권장량 계산
                val userRecommendedKcal =
                    String.format("%.1f", ((userHeight - 100) * 0.9 * userActivityLevel))
                val userData = mutableMapOf<String, Any>(
                    "userAge" to userAgeEditText.text.toString(),
                    "userWeight" to userWeightEditText.text.toString(),
                    "userHeight" to userheightEditText.text.toString(),
                    "userSex" to userSex!!,
                    "userActivityLevel" to userActivityLevel.toString(),
                    "userRecommendedKcal" to userRecommendedKcal
                )


                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(userId)
                    .update(userData)
                    .addOnSuccessListener {
                        activityLevelInfoDialog.dismiss()
                    }
                    .addOnFailureListener {

                    }
                setupUserKcalInfo()
            }
        }
    }


    private fun showFoodRecordRecyclerView(
        showButton: ImageButton,
        layoutFoodBinding: LayoutFoodBinding
    ) {
        val todown_Rotate_Anim =
            AnimationUtils.loadAnimation(requireContext(), R.anim.todown_half_rotate)
        val toup_Rotate_Anim =
            AnimationUtils.loadAnimation(requireContext(), R.anim.toup_half_rotate)

        showButton.setOnClickListener {
            if (layoutFoodBinding.foodRecordRecyclerView.visibility == View.GONE) {
                layoutFoodBinding.foodRecordRecyclerView.visibility = View.VISIBLE
                it.apply {
                    this.startAnimation(todown_Rotate_Anim)
                    this.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24)
                }
            } else if (layoutFoodBinding.foodRecordRecyclerView.visibility == View.VISIBLE) {
                layoutFoodBinding.foodRecordRecyclerView.visibility = View.GONE
                it.apply {
                    this.startAnimation(toup_Rotate_Anim)
                    this.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24)
                }
            }
        }
    }


    private fun moveSearchFood(foodAddImageButton: ImageButton, time: Any) {
        foodAddImageButton.setOnClickListener {
            val mealtime = time.toString()
            val mealTimePosition = when (mealtime) {
                "아침식사" -> 0
                "점심식사" -> 1
                "저녁식사" -> 2
                else -> 3
            }
            bundle.putInt("selectedMealtime", mealTimePosition)

            foodInfoFragment.arguments = bundle
            foodSearchFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodSearchFragment)
        }
    }

    private fun setupUserKcalInfo() {
        val todayTotalKcal = totalFoodList.sumOf { it.kcal!!.format("04d").toDouble() }
        var recommendedKcal: Double
        var remainKcal: Double

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                recommendedKcal = document["userRecommendedKcal"].toString().format("%.1d").toDouble()
                remainKcal = recommendedKcal - todayTotalKcal

                binding.remainKcalTextView.text = String.format("%.1f", remainKcal).plus("kcal")
                binding.todayTotalKcalTextView.text = String.format("%.1f", todayTotalKcal).plus("kcal")
                binding.recommendKcalTextView.text = recommendedKcal.toString().plus("kcal")
            }
            .addOnFailureListener {
                Log.d(TAG, "$it")
            }
    }

    private fun setProgressView() = with(binding) {

        val carbonAmount = String.format("%.1f",totalFoodList.sumOf { it.carbon!!.toDouble() })
        carbonProgressView.labelText = getString(R.string.carbon).plus(": "+carbonAmount+"g")
        carbonProgressView.progress = carbonAmount.toFloat()
        Log.d(TAG,"carbon: $carbonAmount")

        val proteinAmount = String.format("%.1f",totalFoodList.sumOf { it.protein!!.toDouble() })
        proteinProgressView.labelText = getString(R.string.protein).plus(": "+proteinAmount+"g")
        proteinProgressView.progress = proteinAmount.toFloat()
        Log.d(TAG,"protein: $proteinAmount")

        val fatAmount = String.format("%.1f",totalFoodList.sumOf { it.fat!!.toDouble() })
        fatProgressView.labelText = getString(R.string.fat).plus(": "+fatAmount+"g")
        fatProgressView.progress = fatAmount.toFloat()
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayNow
    }

    private fun showProgress() = with(binding){
        progressBar.isVisible = true
    }

    private fun hideProgress() = with(binding) {
        progressBar.isVisible = false
    }

}