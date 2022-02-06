package com.example.k_health.food

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
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
    private val db = FirebaseFirestore.getInstance()
    private val foodSearchFragment = FoodSearchFragment()
    private val foodInfoFragment = FoodInfoFragment()
    private var breakfastFoodList: ArrayList<Item> = arrayListOf()
    private var lunchFoodList: ArrayList<Item> = arrayListOf()
    private var dinnerFoodList: ArrayList<Item> = arrayListOf()
    private var etcFoodList: ArrayList<Item> = arrayListOf()
    private lateinit var breakfastFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var lunchFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var dinnerFoodRecordListAdapter: FoodRecordListAdapter
    private lateinit var etcFoodRecordListAdapter: FoodRecordListAdapter
    private val scope = MainScope()
    private val bundle = Bundle()

    companion object {
        const val TAG = "FoodFragment"
    }


    init {
        /*breakfastFoodList.add(
            Item(
                aNIMALPLANT = "",
                bGNYEAR = "",
                foodName = "아침",
                kcal = "444.00",
                carbon = "42.75",
                protein = "16.80",
                fat = "22.80",
                sugar = "N/A",
                sodium = "927.00",
                cholesterol = "N/A",
                saturatedFattyAcids = "N/A",
                unsaturatedFattyAcids = "N/A",
                gram = "150",
                isSelected = false
            )

        )
        lunchFoodList.add(
            Item(
                aNIMALPLANT = "",
                bGNYEAR = "",
                foodName = "점심",
                kcal = "444.00",
                carbon = "42.75",
                protein = "16.80",
                fat = "22.80",
                sugar = "N/A",
                sodium = "927.00",
                cholesterol = "N/A",
                saturatedFattyAcids = "N/A",
                unsaturatedFattyAcids = "N/A",
                gram = "150",
                isSelected = false
            )

        )
        dinnerFoodList.add(

            Item(
                aNIMALPLANT = "",
                bGNYEAR = "",
                foodName = "저녁",
                kcal = "444.00",
                carbon = "42.75",
                protein = "16.80",
                fat = "22.80",
                sugar = "N/A",
                sodium = "927.00",
                cholesterol = "N/A",
                saturatedFattyAcids = "N/A",
                unsaturatedFattyAcids = "N/A",
                gram = "150",
                isSelected = false
            )

        )
        etcFoodList.add(
            Item(
                aNIMALPLANT = "",
                bGNYEAR = "",
                foodName = "간식,기타",
                kcal = "444.00",
                carbon = "42.75",
                protein = "16.80",
                fat = "22.80",
                sugar = "N/A",
                sodium = "927.00",
                cholesterol = "N/A",
                saturatedFattyAcids = "N/A",
                unsaturatedFattyAcids = "N/A",
                gram = "150",
                isSelected = false
            )

        )*/

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)

        initViews()
        getFoodRecordWithCalendar()

        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        getUserFoodRecord(
            todayNow,
            FoodTime.BREAKFAST.time,
            breakfastFoodList,
            breakfastFoodRecordListAdapter
        )
        getUserFoodRecord(
            todayNow,
            FoodTime.LUNCH.time,
            lunchFoodList,
            lunchFoodRecordListAdapter
        )
        getUserFoodRecord(
            todayNow,
            FoodTime.DINNER.time,
            dinnerFoodList,
            dinnerFoodRecordListAdapter
        )
        getUserFoodRecord(
            todayNow,
            FoodTime.ETC.time,
            etcFoodList,
            etcFoodRecordListAdapter
        )


    }

    @SuppressLint("ResourceAsColor")
    private fun initViews() = with(binding) {
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            breakfastFoodRecordListAdapter = FoodRecordListAdapter(breakfastFoodList!!)
            initRecyclerView(this, breakfastFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            lunchFoodRecordListAdapter = FoodRecordListAdapter(lunchFoodList!!)
            initRecyclerView(this, lunchFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            dinnerFoodRecordListAdapter = FoodRecordListAdapter(dinnerFoodList!!)
            initRecyclerView(this, dinnerFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            etcFoodRecordListAdapter = FoodRecordListAdapter(etcFoodList!!)
            initRecyclerView(this, etcFoodRecordListAdapter)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }

    // TODO 동기처리해보기
    private fun getFoodRecordWithCalendar() {
        binding.foodCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String =
                if (month > 10) "${month + 1}" else String.format("%02d", month + 1)
            val dayOfMonthString: String =
                if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val todayDate = "${year}/${monthString}/${dayOfMonthString}" //
            val selectedDate =
                "${year}${monthString}${dayOfMonthString}" // firestore의 path로 '/'사용불가
            Log.d(TAG, "selectedDate: $selectedDate")


            bundle.putString("todayDate", todayDate)
            bundle.putString("selectedDate", selectedDate)

            foodSearchFragment.arguments = bundle

            getUserFoodRecord(
                selectedDate,
                FoodTime.BREAKFAST.time,
                breakfastFoodList,
                breakfastFoodRecordListAdapter
            )
            getUserFoodRecord(
                selectedDate,
                FoodTime.LUNCH.time,
                lunchFoodList,
                lunchFoodRecordListAdapter
            )
            getUserFoodRecord(
                selectedDate,
                FoodTime.DINNER.time,
                dinnerFoodList,
                dinnerFoodRecordListAdapter
            )
            getUserFoodRecord(
                selectedDate,
                FoodTime.ETC.time,
                etcFoodList,
                etcFoodRecordListAdapter
            )

        }
    }


    private fun initRecyclerView(
        layoutFoodBinding: LayoutFoodBinding,
        foodRecordListAdapter: FoodRecordListAdapter
    ) {
        layoutFoodBinding.foodRecordRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodRecordListAdapter
        }
    }


    private fun showFoodRecordRecyclerView(
        showButton: ImageButton,
        layoutFoodBinding: LayoutFoodBinding
    ) {
        showButton.setOnClickListener {
            if (layoutFoodBinding.foodRecordRecyclerView.visibility == View.VISIBLE)
                layoutFoodBinding.foodRecordRecyclerView.visibility = View.GONE
            else if (layoutFoodBinding.foodRecordRecyclerView.visibility == View.GONE)
                layoutFoodBinding.foodRecordRecyclerView.visibility = View.VISIBLE

        }
    }


    private fun moveSearchFood(foodAddImageButton: ImageButton, time: Any) {
        foodAddImageButton.setOnClickListener {

            bundle.putString("time", time.toString())
            Log.d(TAG, "time : $time")

            foodInfoFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodSearchFragment)
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
                foodRecordListAdapter.notifyDataSetChanged()

            }
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