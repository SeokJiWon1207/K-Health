package com.example.k_health.food

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodFragment : Fragment(R.layout.fragment_food), TimeInterface {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val foodSearchFragment = FoodSearchFragment()
    private val foodInfoFragment = FoodInfoFragment()
    private lateinit var foodRecordListAdapter: FoodRecordListAdapter
    private val bundle = Bundle()
    private var SelectedDate = "20220119"

    companion object {
        const val TAG = "FoodFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)

        setDateToday()
        setFoodTime()


    }

    @SuppressLint("ResourceAsColor")
    private fun setFoodTime() = with(binding){
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            getFoodRecordWithMealtime(setDateToday(), FoodTime.BREAKFAST.time, this)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            getFoodRecordWithMealtime(setDateToday(), FoodTime.LUNCH.time, this)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            getFoodRecordWithMealtime(setDateToday(), FoodTime.DINNER.time, this)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            getFoodRecordWithMealtime(setDateToday(), FoodTime.ETC.time, this)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }

    // TODO 날짜 반환하기
    private fun setDateToday(): String {
        binding.foodCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String = if (month > 10) "${month+1}" else String.format("%02d", month+1)
            val dayOfMonthString: String = if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val todayDate = "${year}/${monthString}/${dayOfMonthString}" //
            val selectedDate = "${year}${monthString}${dayOfMonthString}" // firestore의 path로 '/'사용불가
            Log.d(TAG, "selectedDate: $selectedDate")
            SelectedDate = selectedDate
            bundle.putString("todayDate", todayDate)
            bundle.putString("selectedDate", selectedDate)

            foodSearchFragment.arguments = bundle
        }

        return SelectedDate
    }

    private fun getFoodRecordWithMealtime(selectedDate: String, mealtime: String, layoutFoodBinding: LayoutFoodBinding) {
        val recordedFoodList: ArrayList<Item> = arrayListOf()

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(Repository.userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(selectedDate) // 캘린더 선택 날짜
            .collection(mealtime) // 식사 구분
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recordedFoodList.clear()
                for (snapshot in querySnapshot!!.documents) {
                    var foodRecordItem = snapshot.toObject(Item::class.java)
                    recordedFoodList.add(foodRecordItem!!)
                }
                foodRecordListAdapter.notifyDataSetChanged()
            }

        Log.d(TAG,"recordedFoodList: $recordedFoodList")

        foodRecordListAdapter = FoodRecordListAdapter(recordedFoodList)

        layoutFoodBinding.foodRecordRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodRecordListAdapter
        }
    }

    private fun showFoodRecordRecyclerView(showButton: ImageButton, layoutFoodBinding: LayoutFoodBinding) {
        showButton.setOnClickListener {
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

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayNow
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}