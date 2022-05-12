package com.example.k_health.food

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.*
import com.example.k_health.databinding.FragmentFoodSearchBinding
import com.example.k_health.food.adapter.FoodHistoryAdapter
import com.example.k_health.food.adapter.FoodListAdapter
import com.example.k_health.health.TimeInterface
import com.example.k_health.food.data.models.History
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FoodSearchFragment : Fragment(R.layout.fragment_food_search), TimeInterface {

    companion object {
        const val TAG = "FoodSearchFragment"
    }

    private var _binding: FragmentFoodSearchBinding? = null
    private val binding get() = _binding!!
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private val db = FirebaseFirestore.getInstance()
    private val scope = MainScope()
    private val foodInfoFragment = FoodInfoFragment()
    private val bundle = Bundle()
    private lateinit var foodListAdapter: FoodListAdapter
    private lateinit var foodHistoryAdapter: FoodHistoryAdapter
    private lateinit var roomDB: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFoodSearchBinding.bind(view)

        roomDB = getAppDatabase(requireContext())

        recordFoods()
        setupSpinnerMealtime()
        setupSpinnerHandler()
        initFoodRecyclerView()
        initSearchEditText()
        initHistoryRecyclerView()
        fetchFoodItems()


    }

    private fun recordFoods() = with(binding) {
        val today = timeGenerator()
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val selectedDate = arguments?.getString("selectedDate") ?: todayNow
        foodDateTextView.text = arguments?.getString("todayDate") ?: today

        enrollButton.setOnClickListener {
            val mealtime = spinner.selectedItem.toString()
            val foodSelectedList = foodListAdapter.checkedList.filter { it.isSelected == true }
            val foodRecordData = mutableMapOf<String, Any>()

            if (foodSelectedList.isEmpty()) {
                Repository.showSnackBar(
                    requireView(),
                    "선택된 식사가 없습니다. \n식사를 선택해주세요."
                )
                return@setOnClickListener
            } else {

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
                    foodRecordData["saturatedFattyAcids"] =
                        foodSelectedList[i].saturatedFattyAcids.toString()
                    foodRecordData["unsaturatedFattyAcids"] =
                        foodSelectedList[i].unsaturatedFattyAcids.toString()

                    db.collection(DBKey.COLLECTION_NAME_USERS)
                        .document(userId)
                        .collection(DBKey.COLLECTION_NAME_FOODRECORD) // 식사기록보관
                        .document(selectedDate) // 선택 날짜
                        .collection(mealtime) // 현재 선택한 식사 시간
                        .document(foodSelectedList[i].foodName!!)
                        .set(foodRecordData) // 식사 데이터
                        .addOnSuccessListener {
                            if (i.equals(foodSelectedList.size - 1)) { // 마지막 데이터를 넣을 때 스낵바 호출
                                Snackbar.make(
                                    requireView(),
                                    "${mealtime}가 등록되었습니다.",
                                    Snackbar.LENGTH_INDEFINITE
                                )
                                    .setAction("확인", object : View.OnClickListener {
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
    }

    // 스피너 아이템 등록
    private fun setupSpinnerMealtime() = with(binding) {
        val mealtime = resources.getStringArray(R.array.spinner_mealtime)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealtime)
        val selectedMealtime = arguments?.getInt("selectedMealtime")
        Log.d(TAG, "selectedMealtime: ${arguments?.getInt("selectedMealtime")}")
        spinner.adapter = adapter
        spinner.setSelection(selectedMealtime!!)
    }

    // 스피너 이벤트 관련 핸들러
    private fun setupSpinnerHandler() = with(binding) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // 아이템이 선택되었을때
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bundle.putString("mealtime", spinner.selectedItem.toString())

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
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodListAdapter
        }
    }

    // Retrofit 1-4) FoodApiService의 Method 사용으로 받아온 데이터를 RecyclerView에 뿌려주기
    private fun fetchFoodItems(query: String? = null) = scope.launch {
        try {
            showProgress()
            showFoodListView()
            hideHistoryView()
            hideAlertTextView()
            hideRetryButton()
            Repository.getFoodItems()?.let {
                (binding.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    Log.d(TAG, "items : ${it.body!!.items}")
                    submitList(it.body.items!!.filterNot { it.gram == "0" || it.foodName == null })
                    hideRetryButton()
                }
                hideProgress()
            }
        } catch (exception: Exception) {
            hideProgress()
            showRetryButton()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEditText() = with(binding) {
        searchEditText.apply {
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                    hideHistoryView()
                    search(this.text.toString())
                    showFoodListView()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            /*ACTION_DOWN : 처음 눌렸을 때
            ACTION_MOVE : 누르고 움직였을 때
            ACTION_UP : 누른걸 땠을 때*/
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideFoodListView()
                    showHistoryView()
                }
                return@setOnTouchListener false
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
                if (it.body!!.totalCount == 0) {
                    hideFoodListView()
                    showAlertTextView(keyword)
                } else {
                    (binding.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                        hideHistoryView()
                        hideAlertTextView()
                        showFoodListView()
                        saveSearchKeyword(keyword)
                        submitList(it.body.items?.filterNot { it.gram == "0" })
                    }
                }
            }
        } catch (e: Exception) {

        }
    }


    private fun searchEditTextClear() = with(binding) {
        editTextClearButton.setOnClickListener {
            Log.d(TAG, "clear")
            searchEditText.text!!.clear()
        }
    }

    private fun showProgress() = with(binding) {
        progressBar.isVisible = true
    }

    private fun hideProgress() = with(binding) {
        progressBar.isVisible = false
    }

    private fun showRetryButton() {
        binding.retryButton.apply {
            isVisible = true
            setOnClickListener {
                fetchFoodItems()
            }
        }
    }

    private fun hideRetryButton() = with(binding) {
        retryButton.isVisible = false
    }

    // 검색한 기록을 보여주는 메소드
    private fun showHistoryView() {
        // Room 2-5) MainThread 에서 Room DB에 접근하려고 하면 에러가 발생한다.
        // Room과 관련된 액션은 Thread, Coroutine 등을 이용해 백그라운드에서 작업해야 한다.
        CoroutineScope(Dispatchers.IO).launch {
            scope.launch {
                // 중복 검색어 제거
                roomDB.historyDao().getAll().distinctBy { it.keyword }.reversed().run {
                    foodHistoryAdapter.submitList(this)
                    binding.historyRecyclerView.isVisible = true
                }
            }
        }
    }

    private fun initHistoryRecyclerView() = with(binding) {
        foodHistoryAdapter = FoodHistoryAdapter(histroyDeleteClickedListener = {
            deleteSearchKeyword(it)
        }, itemClickListener = {
            searchEditText.setText(it.keyword.toString())
            search(it.keyword!!)
        })
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = foodHistoryAdapter

        initSearchEditText()
    }

    private fun hideHistoryView() = with(binding) {
        historyRecyclerView.isVisible = false
    }

    private fun showFoodListView() = with(binding) {
        foodRecyclerView.isVisible = true
    }

    private fun hideFoodListView() = with(binding) {
        foodRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            roomDB.historyDao().insertHistory(History(null, keyword))
        }
    }

    private fun deleteSearchKeyword(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            roomDB.historyDao().delete(keyword)
            showHistoryView()
        }
    }

    private fun showAlertTextView(searchKeyword: String) = with(binding) {
        alertTextView.apply {
            isVisible = true
            alertTextView.text = searchKeyword + getString(R.string.label_alerttext)
        }
    }

    private fun hideAlertTextView() = with(binding) {
        alertTextView.isVisible = false
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))

        return todayNow
    }

}