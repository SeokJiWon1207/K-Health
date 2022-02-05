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
    private val recordedFoodList: ArrayList<Item> = arrayListOf()
    private var breakfastFoodmap = mutableMapOf<String, ArrayList<Item>>()
    private var lunchFoodmap = mutableMapOf<String, ArrayList<Item>>()
    private var dinnerFoodmap = mutableMapOf<String, ArrayList<Item>>()
    private var etcFoodmap = mutableMapOf<String, ArrayList<Item>>()
    private lateinit var foodRecordListAdapter1: FoodRecordListAdapter
    private lateinit var foodRecordListAdapter2: FoodRecordListAdapter
    private lateinit var foodRecordListAdapter3: FoodRecordListAdapter
    private lateinit var foodRecordListAdapter4: FoodRecordListAdapter
    private val scope = MainScope()
    private val bundle = Bundle()

    companion object {
        const val TAG = "FoodFragment"
    }


    init {
        breakfastFoodmap.put(
            "아침식사",
            arrayListOf(
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
        )
        lunchFoodmap.put(
            "점심식사",
            arrayListOf(
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
        )
        dinnerFoodmap.put(
            "저녁식사",
            arrayListOf(
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
        )
        etcFoodmap.put(
            "간식,기타",
            arrayListOf(
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
            )
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFoodBinding.bind(view)

        initViews()
        getFoodRecordWithBreakfast()


    }

    @SuppressLint("ResourceAsColor")
    private fun initViews() = with(binding) {
        with(layoutBreakfast) {
            timeImageView.setImageResource(FoodTime.BREAKFAST.timeImage)
            timeTextView.text = FoodTime.BREAKFAST.time
            timeTextView.setTextColor(FoodTime.BREAKFAST.textColor)
            foodRecordListAdapter1 = FoodRecordListAdapter(breakfastFoodmap[FoodTime.BREAKFAST.time]!!)
            initRecyclerView(this, foodRecordListAdapter1)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.BREAKFAST.time)
        }
        with(layoutLunch) {
            timeImageView.setImageResource(FoodTime.LUNCH.timeImage)
            timeTextView.text = FoodTime.LUNCH.time
            timeTextView.setTextColor(FoodTime.LUNCH.textColor)
            foodRecordListAdapter2 = FoodRecordListAdapter(lunchFoodmap[FoodTime.LUNCH.time]!!)
            initRecyclerView(this, foodRecordListAdapter2)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.LUNCH.time)
        }
        with(layoutDinner) {
            timeImageView.setImageResource(FoodTime.DINNER.timeImage)
            timeTextView.text = FoodTime.DINNER.time
            timeTextView.setTextColor(FoodTime.DINNER.textColor)
            foodRecordListAdapter3 = FoodRecordListAdapter(dinnerFoodmap[FoodTime.DINNER.time]!!)
            initRecyclerView(this, foodRecordListAdapter3)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.DINNER.time)
        }
        with(layoutEtc) {
            timeImageView.setImageResource(FoodTime.ETC.timeImage)
            timeTextView.text = FoodTime.ETC.time
            timeTextView.setTextColor(FoodTime.ETC.textColor)
            foodRecordListAdapter4 = FoodRecordListAdapter(etcFoodmap[FoodTime.ETC.time]!!)
            initRecyclerView(this, foodRecordListAdapter4)
            showFoodRecordRecyclerView(foodRecordOpenImageButton, this)
            moveSearchFood(foodAddImageButton, FoodTime.ETC.time)
        }
    }

    // TODO 동기처리해보기
    private fun getFoodRecordWithBreakfast() {
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


            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                .document(selectedDate) // 캘린더 선택 날짜
                .collection(FoodTime.BREAKFAST.time) // 식사 구분
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    recordedFoodList.clear()
                    breakfastFoodmap.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val foodRecordItem = snapshot.toObject(Item::class.java)
                        recordedFoodList.add(foodRecordItem!!)
                    }
                    breakfastFoodmap.put(FoodTime.BREAKFAST.time, recordedFoodList)
                    Log.d(TAG, "breakfastMap: $breakfastFoodmap")
                    foodRecordListAdapter1.notifyDataSetChanged()

                }


            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                .document(selectedDate) // 캘린더 선택 날짜
                .collection(FoodTime.LUNCH.time) // 식사 구분
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    recordedFoodList.clear()
                    lunchFoodmap.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val foodRecordItem = snapshot.toObject(Item::class.java)
                        recordedFoodList.add(foodRecordItem!!)
                    }
                    lunchFoodmap.put(FoodTime.LUNCH.time, recordedFoodList)

                    Log.d(TAG, "lunchMap: $lunchFoodmap")
                    foodRecordListAdapter2.notifyDataSetChanged()

                }



            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                .document(selectedDate) // 캘린더 선택 날짜
                .collection(FoodTime.DINNER.time) // 식사 구분
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    recordedFoodList.clear()
                    dinnerFoodmap.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val foodRecordItem = snapshot.toObject(Item::class.java)
                        recordedFoodList.add(foodRecordItem!!)
                    }
                    dinnerFoodmap.put(FoodTime.DINNER.time, recordedFoodList)
                    Log.d(TAG, "dinnerMap: $dinnerFoodmap")
                    foodRecordListAdapter3.notifyDataSetChanged()

                }

            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                .document(selectedDate) // 캘린더 선택 날짜
                .collection(FoodTime.ETC.time) // 식사 구분
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    recordedFoodList.clear()
                    etcFoodmap.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val foodRecordItem = snapshot.toObject(Item::class.java)
                        recordedFoodList.add(foodRecordItem!!)
                    }
                    etcFoodmap.put(FoodTime.ETC.time, recordedFoodList)
                    Log.d(TAG, "etcMap: $etcFoodmap")
                    foodRecordListAdapter4.notifyDataSetChanged()
                }
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
            layoutFoodBinding.foodRecordRecyclerView.visibility = View.VISIBLE
            Log.d(TAG, "clicked")
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

    private fun showProgress() {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
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