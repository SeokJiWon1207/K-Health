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
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.FragmentFoodSearchBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FoodSearchFragment : Fragment(R.layout.fragment_food_search) {

    companion object {
        const val TAG = "FoodSearchFragment"
    }

    private var _binding: FragmentFoodSearchBinding? = null
    private val binding get() = _binding!!
    private val scope = MainScope()
    private val foodInfoFragment = FoodInfoFragment()
    private lateinit var foodListAdapter: FoodListAdapter
    private val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFoodSearchBinding.bind(view)

        Log.d(TAG, "onViewCreated")

        setupSpinnerMealtime()
        setupSpinnerHandler()
        initFoodRecyclerView()
        fetchFoodItems()
        initSearchEditText()

        binding.enrollButton.setOnClickListener {
            var checkedListSize = foodListAdapter.checkedList.size
            binding.enrollButton.text = "등록하기".plus("(${checkedListSize})")
            Log.d(TAG, "${foodListAdapter.checkedList}")

        }

    }

    private fun setupSpinnerMealtime() {
        val mealtime = resources.getStringArray(R.array.spinner_mealtime)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealtime)
        binding.spinner.adapter = adapter
    }

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
                Log.d(TAG, binding.spinner.selectedItem.toString())

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
        searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        searchEditText.addTextChangedListener(object: TextWatcher {
            // 텍스트 변경 중 호출
            override fun afterTextChanged(s: Editable?) {
                if (searchEditText.text.length > 0) {
                    editTextClearButton.visibility = View.VISIBLE
                } else {
                    editTextClearButton.visibility = View.GONE
                }
            }

            // 텍스트 변경 전 호출
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // 텍스트 변경 후 호출
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        editTextClearButton.setOnClickListener {
            searchEditTextClear()
        }
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

    private fun searchEditTextClear() {
        binding.searchEditText.text.clear()
    }

    override fun onResume() {
        super.onResume()
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