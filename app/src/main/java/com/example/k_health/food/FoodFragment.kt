package com.example.k_health.food

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.ActivitylevelinfoDialogBinding
import com.example.k_health.databinding.FragmentFoodBinding
import com.example.k_health.databinding.LayoutFoodBinding
import com.example.k_health.food.data.models.Item
import com.example.k_health.health.TimeInterface
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodFragment : Fragment(R.layout.fragment_food), TimeInterface {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private var _activitylevelinfoDialogBinding: ActivitylevelinfoDialogBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private val foodSearchFragment = FoodSearchFragment()
    private val foodInfoFragment = FoodInfoFragment()
    val breakfastFoodList: ArrayList<Item> = arrayListOf()
    val lunchFoodList: ArrayList<Item> = arrayListOf()
    val dinnerFoodList: ArrayList<Item> = arrayListOf()
    val etcFoodList: ArrayList<Item> = arrayListOf()
    private lateinit var breakfastFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var lunchFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var dinnerFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var etcFoodRecordListAdapter: FoodRecordListAdapter
    private val activityLevelInfoDialog: Dialog by lazy { Dialog(requireContext()) }
    private val scope = MainScope()
    private val bundle = Bundle()

    companion object {
        const val TAG = "FoodFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)

        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        initViews()
        getUserFoodRecord(todayNow, FoodTime.BREAKFAST.time, breakfastFoodList, breakfastFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.LUNCH.time, lunchFoodList, lunchFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.DINNER.time, dinnerFoodList, dinnerFoodRecordListAdapter)
        getUserFoodRecord(todayNow, FoodTime.ETC.time, etcFoodList, etcFoodRecordListAdapter)

        /*Log.d(TAG,"breakfast: $breakfastFoodList")
        Log.d(TAG,"lunch: $lunchFoodList")
        Log.d(TAG,"dinner: $dinnerFoodList")
        Log.d(TAG,"etc: $etcFoodList")*/
        isActivityLevelNotNull()
        getFoodRecordWithCalendar()
        getTodayKcal()



    }

    @SuppressLint("ResourceAsColor")
    private fun initViews() = with(binding) {
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            breakfastFoodRecordListAdapter = FoodRecordListAdapter(breakfastFoodList, onClickDeleteButton = {
                //3. onBindViewHolder에서 listposition을 전달받고 이 함수를 실행하게 된다.
                // deleteRecord 함수가 포지션값인 it을 받고 지운다.
                deleteRecord(it, breakfastFoodList, this.foodRecordRecyclerView, FoodTime.BREAKFAST.time)
            })
            initRecyclerView(foodRecordRecyclerView, breakfastFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            lunchFoodRecordListAdapter = FoodRecordListAdapter(lunchFoodList, onClickDeleteButton = {
                deleteRecord(it, lunchFoodList, this.foodRecordRecyclerView, FoodTime.LUNCH.time)
            })
            initRecyclerView(foodRecordRecyclerView, lunchFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            dinnerFoodRecordListAdapter = FoodRecordListAdapter(dinnerFoodList, onClickDeleteButton = {
                deleteRecord(it, dinnerFoodList, this.foodRecordRecyclerView, FoodTime.DINNER.time)
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

    private fun deleteRecord(item: Item, foodlist: ArrayList<Item>, recyclerView: RecyclerView, mealtime: String) {
        foodlist.remove(item)
        val pref = activity?.getSharedPreferences("pref", 0)
        val toremovedDate = pref?.getString("selectedDate","YYYY")
        val toremovedFoodname = item.foodName
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(Repository.userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(toremovedDate!!) // 캘린더 선택 날짜
            .collection(mealtime) // 식사 구분
            .document(toremovedFoodname!!)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG,"removed item")
            }
            .addOnFailureListener { e ->
                Log.d(TAG,"removed failed : $e")
            }

        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getFoodRecordWithCalendar() {
        val pref = activity?.getSharedPreferences("pref", 0)
        val edit = pref?.edit()
        binding.foodCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String =
                if (month > 10) "${month + 1}" else String.format("%02d", month + 1)
            val dayOfMonthString: String =
                if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val todayDate = "${year}/${monthString}/${dayOfMonthString}" //
            val selectedDate =
                "${year}${monthString}${dayOfMonthString}" // firestore의 path로 '/'사용불가
            Log.d(TAG,"selectedDate: $selectedDate")

            edit?.putString("selectedDate",selectedDate)?.apply() // sharedpreference에 값 저장

            bundle.putString("todayDate", todayDate)
            bundle.putString("selectedDate", selectedDate)

            foodSearchFragment.arguments = bundle

            getUserFoodRecord(selectedDate, FoodTime.BREAKFAST.time, breakfastFoodList, breakfastFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.LUNCH.time, lunchFoodList, lunchFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.DINNER.time, dinnerFoodList, dinnerFoodRecordListAdapter)
            getUserFoodRecord(selectedDate, FoodTime.ETC.time, etcFoodList, etcFoodRecordListAdapter)
            Log.d(TAG,"breakfast: $breakfastFoodList")
            Log.d(TAG,"lunch: $lunchFoodList")
            Log.d(TAG,"dinner: $dinnerFoodList")
            Log.d(TAG,"etc: $etcFoodList")

            getTodayKcal()

        }
    }

    private fun getUserFoodRecord(
        selectedDate: String,
        mealtime: String,
        foodlist: ArrayList<Item>,
        foodRecordListAdapter: FoodRecordListAdapter
    ) {
        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(Repository.userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(selectedDate) // 캘린더 선택 날짜
            .collection(mealtime) // 식사 구분
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                foodlist.clear()
                for (snapshot in querySnapshot!!.documents) {
                    val foodRecordItem = snapshot.toObject(Item::class.java)
                    foodlist.add(foodRecordItem!!)
                }
                Log.d(TAG,"foodlist: $foodlist")
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
            .document(Repository.userId)
            .get()
            .addOnSuccessListener { document ->
                if (document["activityLevel"] != null) {
                    Log.d(TAG, "activityLevel : ${document["activityLevel"]}")
                } else {
                    showActivityLevelInputPopup()
                }
            }
            .addOnFailureListener { Error ->
                Log.d("Error", "Error : $Error")
            }
    }

    private fun showActivityLevelInputPopup()  {
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
        val userWeightEditText = activityLevelInfoDialog.findViewById<EditText>(R.id.user_weight_EditText)
        val radioGroup = activityLevelInfoDialog.findViewById<RadioGroup>(R.id.sex_RadioGroup)
        val man = activityLevelInfoDialog.findViewById<RadioButton>(R.id.sex_man)
        val woman = activityLevelInfoDialog.findViewById<RadioButton>(R.id.sex_woman)
        var userSex: String = ""
        val submitButton = activityLevelInfoDialog.findViewById<Button>(R.id.submitButton)
        
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.sex_man -> userSex = man.text.toString()
                else -> userSex = woman.text.toString()
            }
        }

        submitButton.setOnClickListener {
            val userData = mutableMapOf<String, Any>(
                "userAge" to userAgeEditText.text.toString(),
                "userWeight" to userWeightEditText.text.toString(),
                "userSex" to userSex
            )

            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .update(userData)
                .addOnSuccessListener {
                    activityLevelInfoDialog.dismiss()
                }
                .addOnFailureListener {

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
            val mealTimePosition = when(mealtime) {
                "아침식사" -> 0
                "점심식사" -> 1
                "저녁식사" -> 2
                else -> 3
            }
            bundle.putInt("selectedMealtime", mealTimePosition)
            Log.d(TAG, "selectedMealtime : $mealTimePosition")

            foodInfoFragment.arguments = bundle
            foodSearchFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodSearchFragment)
        }
    }



    private fun getTodayKcal() {
        val breakfastKcal = breakfastFoodList.sumOf { it.kcal!!.format("04d").toDouble() }
        val lunchKcal = lunchFoodList.sumOf { it.kcal!!.format("04d").toDouble() }
        val dinnerKcal = dinnerFoodList.sumOf { it.kcal!!.format("04d").toDouble() }
        val etcKcal = etcFoodList.sumOf { it.kcal!!.format("04d").toDouble() }

        val todayTotalKcal = breakfastKcal + lunchKcal + dinnerKcal + etcKcal
        Log.d(TAG,"todaykcal: $todayTotalKcal")
        binding.todayTotalKcalTextView.text = todayTotalKcal.toString().plus("kcal")
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayNow
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scope.cancel()
    }
}