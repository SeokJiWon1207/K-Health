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

    companion object {
        const val TAG = "FoodFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)

        setFoodTime()
        setDateToday()



    }

    @SuppressLint("ResourceAsColor")
    private fun setFoodTime() = with(binding){
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            foodRecordOpenImageButton.setOnClickListener {
                foodRecordRecyclerView.visibility = View.VISIBLE
            }
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }

    private fun setDateToday() {
        binding.foodCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String = if (month > 10) "${month+1}" else String.format("%02d", month+1)
            val dayOfMonthString: String = if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val todayDate = "${year}/${monthString}/${dayOfMonthString}" //
            val dbtodayDate = "${year}${monthString}${dayOfMonthString}" // firestore의 path로 '/'사용불가
            Log.d(TAG, "${year}/${monthString}/${dayOfMonthString}")

            setBreakfastRecord(dbtodayDate)

            bundle.putString("todayDate", todayDate)

            foodSearchFragment.arguments = bundle
        }
    }

    private fun setBreakfastRecord(today: String) {
        val breakfastList: ArrayList<Item> = arrayListOf()

        db.collection(DBKey.COLLECTION_NAME_USERS)
            .document(Repository.userId)
            .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
            .document(today) // 당일 날짜
            .collection("아침식사") // 현재 선택한 식사 시간
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            breakfastList.clear()
                for (snapshot in querySnapshot!!.documents) {
                    var foodRecordItem = snapshot.toObject(Item::class.java)
                    breakfastList.add(foodRecordItem!!)
                }
                foodRecordListAdapter.notifyDataSetChanged()
            }

        Log.d(TAG,"breakfastList: $breakfastList")

        foodRecordListAdapter = FoodRecordListAdapter(breakfastList)

        binding.layoutBreakfast.foodRecordRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodRecordListAdapter
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