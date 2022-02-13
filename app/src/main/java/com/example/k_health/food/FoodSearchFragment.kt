package com.example.k_health.food

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.FragmentFoodSearchBinding
import com.example.k_health.health.TimeInterface
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodSearchFragment : Fragment(R.layout.fragment_food_search), TimeInterface {

    companion object {
        const val TAG = "FoodSearchFragment"
    }

    private var _binding: FragmentFoodSearchBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val scope = MainScope()
    private val foodInfoFragment = FoodInfoFragment()
    private lateinit var foodListAdapter: FoodListAdapter
    private val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFoodSearchBinding.bind(view)

        Log.d(TAG, "onViewCreated")

        recordFoods()
        setupSpinnerMealtime()
        setupSpinnerHandler()
        initFoodRecyclerView()
        fetchFoodItems()
        initSearchEditText()


    }

    private fun recordFoods() = with(binding) {
        val today = timeGenerator()
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val selectedDate = arguments?.getString("selectedDate") ?: todayNow
        foodDateTextView.text = arguments?.getString("todayDate") ?: today

        Log.d(TAG, "selectedDate: $selectedDate")

        enrollButton.setOnClickListener {
            val mealtime = spinner.selectedItem.toString()
            val foodSelectedList = foodListAdapter.checkedList.filter { it.isSelected == true }
            val foodRecordData = mutableMapOf<String, Any>()

            if (foodSelectedList.isEmpty()) {
                Snackbar.make(requireView(), "선택된 식사가 없습니다. \n식사를 선택해주세요", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", object: View.OnClickListener {
                        override fun onClick(v: View?) {
                        //
                        }
                    })
                    .show()
            }

            for (i in foodSelectedList.indices) {

                foodRecordData["foodName"] = foodSelectedList[i].foodName.toString()
                foodRecordData["gram"] = foodSelectedList[i].gram.toString()
                foodRecordData["kcal"] = foodSelectedList[i].kcal.toString()
                foodRecordData["carbon"] = foodSelectedList[i].carbon.toString()
                foodRecordData["protein"] = foodSelectedList[i].protein.toString()
                foodRecordData["fat"] = foodSelectedList[i].fat.toString()
                foodRecordData["cholesterol"] = foodSelectedList[i].cholesterol.toString()
                foodRecordData["sodium"] = foodSelectedList[i].sodium.toString()
                foodRecordData["sugar"] = foodSelectedList[i].sugar.toString()
                foodRecordData["saturatedFattyAcids"] = foodSelectedList[i].saturatedFattyAcids.toString()
                foodRecordData["unsaturatedFattyAcids"] = foodSelectedList[i].unsaturatedFattyAcids.toString()

                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(Repository.userId)
                    .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                    .document(selectedDate) // 선택 날짜
                    .collection(mealtime) // 현재 선택한 식사 시간
                    .document(foodSelectedList[i].foodName!!)
                    .set(foodRecordData) // 식사 데이터
                    .addOnSuccessListener {
                        if (i.equals(foodSelectedList.size - 1)) { // 마지막 데이터를 넣을 때 스낵바 호출
                            Snackbar.make(requireView(), "${mealtime}가 등록되었습니다.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("확인", object: View.OnClickListener {
                                    override fun onClick(v: View?) {
                                        val foodFragment = FoodFragment()
                                        (activity as MainActivity).replaceFragment(foodFragment)
                                    }
                                })
                                .show()
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.d(TAG, "error : $error")
                    }
            }
        }
    }

    // 스피너 아이템 등록
    private fun setupSpinnerMealtime() {
        val mealtime = resources.getStringArray(R.array.spinner_mealtime)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealtime)
        val selectedMealtime = arguments?.getInt("selectedMealtime")
        Log.d(TAG,"selectedMealtime: ${arguments?.getInt("selectedMealtime")}")
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(selectedMealtime!!)
    }

    // 스피너 이벤트 관련 핸들러
    private fun setupSpinnerHandler() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 아이템이 선택되었을때
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bundle.putString("mealtime", binding.spinner.selectedItem.toString())
                Log.d(TAG, "mealtime: ${binding.spinner.selectedItem.toString()}")

                foodInfoFragment.arguments = bundle
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }

    private fun initFoodRecyclerView() {
        foodListAdapter = FoodListAdapter(itemClickListener = {
            bundle.putParcelable("item", it) // 아이템을 클릭했을 때 직렬화한 아이템을 하나로 보냄

            foodInfoFragment.arguments = bundle

            (activity as MainActivity).replaceFragment(foodInfoFragment)


        })
        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodListAdapter
        }
    }

    // Retrofit 1-4) FoodApiService의 Method 사용으로 받아온 데이터를 RecyclerView에 뿌려주기
    private fun fetchFoodItems(query: String? = null) = scope.launch {
        try {
            Repository.getFoodItems()?.let {
                (binding.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    Log.d(TAG, "items : ${it.body!!.items}")
                    submitList(it.body.items!!)
                }
            }
        } catch (exception: Exception) {
            Log.d(TAG, "exception : $exception")
        }
    }

    private fun initSearchEditText() = with(binding) {
        searchEditText.apply {
            setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                    search(binding.searchEditText.text.toString())
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            addTextChangedListener(object : TextWatcher {
                // 텍스트 변경 중 호출
                override fun afterTextChanged(s: Editable?) {
                    if (text!!.length > 0) {
                        editTextClearButton.visibility = View.VISIBLE
                    } else {
                        editTextClearButton.visibility = View.GONE
                    }
                }

                // 텍스트 변경 전 호출
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                // 텍스트 변경 후 호출
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            })
        }

        searchEditTextClear()
    }

    private fun search(keyword: String) = scope.launch {
        try {
            Repository.getFoodByName(keyword)?.let {
                (binding.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    Log.d(TAG, "keyword items : ${it.body!!.items}")

                    submitList(it.body.items!!)
                }
            }
        } catch (exception: Exception) {
            Log.d(TAG, "exception : $exception")
        }
    }

    private fun searchEditTextClear() = with(binding) {
        editTextClearButton.setOnClickListener {
            Log.d(TAG,"clear")
            searchEditText.text!!.clear()
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayNow
    }

    override fun onResume() {
        super.onResume()
        setupSpinnerMealtime()
        fetchFoodItems()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchEditTextClear()
         _binding = null
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

}