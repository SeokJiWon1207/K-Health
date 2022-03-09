package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.databinding.FragmentHealthBinding
import com.example.k_health.food.FoodFragment
import com.example.k_health.health.adapter.GetHealthListAdapter
import com.example.k_health.health.model.HealthRecord
import com.example.k_health.health.model.UserHealthRecord
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HealthFragment : Fragment(R.layout.fragment_health), TimeInterface {

    companion object {
        const val TAG = "HealthFragment"
    }

    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val healthListFragment = HealthListFragment()
    private val healthRecordData = arrayListOf<UserHealthRecord>()
    private val getHealthListAdapter = GetHealthListAdapter(healthRecordData, onClickDeleteButton = {
        deleteRecord(it, healthRecordData)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHealthBinding.bind(view)

        initRecyclerView()
        getUserHealthRecord(timeGenerator())
        moveHealthList()
        setDateToday()


    }

    private fun initRecyclerView() = with(binding) {
        healthRecordRecyclerView.apply {
            adapter = getHealthListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setDateToday() {
        val pref = activity?.getSharedPreferences("pref", 0)
        val edit = pref?.edit()
        edit!!.putString("selectedHealthDate", timeGenerator()).apply() // 기본값을 오늘 날짜로 설정
        binding.healthCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthString: String =
                if (month > 10) "${month + 1}" else String.format("%02d", month + 1)
            val dayOfMonthString: String =
                if (dayOfMonth >= 10) "$dayOfMonth" else String.format("%02d", dayOfMonth)
            val selectedHealthDate = "${year}${monthString}${dayOfMonthString}"
            edit!!.putString("selectedHealthDate", selectedHealthDate).apply()

            getUserHealthRecord(selectedHealthDate)

            binding.todayDateTextView.text = "${year}/${monthString}/${dayOfMonthString}"
        }
    }

    private fun getUserHealthRecord(
        selectedDate: String,
    ) {
        hideView()
        val pref = activity?.getSharedPreferences("pref", 0)
        val todayHealthNameList = pref?.getStringSet(selectedDate, mutableSetOf("", ""))!!.toList()
        Log.d(TAG, "todayHealthNameList: $todayHealthNameList")

        try {
            for (i in todayHealthNameList.indices) {
                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(Repository.userId)
                    .collection(DBKey.COLLECTION_NAME_HEALTHRECORD) // 식사기록보관
                    .document(selectedDate)
                    .collection(todayHealthNameList[i])
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        // healthRecordData.clear()
                        for (snapshot in querySnapshot!!.documents) {
                            val healthRecordItem = snapshot.toObject(HealthRecord::class.java)
                            healthRecordData.add(UserHealthRecord(todayHealthNameList[i],healthRecordItem!!.set, healthRecordItem!!.weight, healthRecordItem!!.count))
                            hideAlertText()
                            getHealthListAdapter.notifyDataSetChanged()
                        }
                    }
            }
        } catch (e: Exception) {

        } finally {
            healthRecordData.clear()
            getHealthListAdapter.notifyDataSetChanged()
            showAlertText()
            hideProgress()
        }
    }


    private fun deleteRecord(
        userHealthRecord: UserHealthRecord,
        userHealthList: ArrayList<UserHealthRecord>,
    ) {
        showView()
        showProgress()

        val pref = activity?.getSharedPreferences("pref", 0)
        val toremovedDate = pref?.getString("selectedHealthDate", "YYYY")
        val toremovedName = userHealthRecord.name
        val toremovedSize = userHealthList.filter { it.name == toremovedName }.size
        Log.d(TAG,"before currentList: $userHealthList")

        repeat(toremovedSize) {
            userHealthList.removeIf { it.name == toremovedName }
            Log.d(TAG, "after currentList: $userHealthList")
        }

        val todayHealthSet = pref?.getStringSet(toremovedDate, mutableSetOf("", ""))
        todayHealthSet!!.remove(toremovedName)

        for (i in 1..toremovedSize) {
            db.collection(DBKey.COLLECTION_NAME_USERS)
                .document(Repository.userId)
                .collection(DBKey.COLLECTION_NAME_HEALTHRECORD) // 운동기록보관
                .document(toremovedDate!!) // 캘린더 선택 날짜
                .collection(toremovedName) // 삭제할 운동 이름
                .document(i.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "before userHealthList: ${userHealthList}")
                    userHealthList.removeIf { it.name == toremovedName }
                    Log.d(TAG, "after userHealthList: ${userHealthList}")
                    if (i==toremovedSize) {
                        getHealthListAdapter.notifyDataSetChanged()
                        hideView()
                        hideProgress()
                    }
                    if (healthRecordData.isEmpty()) showAlertText() else hideAlertText()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "removed failed : $e")
                }
        }
    }

    private fun moveHealthList() {
        binding.healthListFloatingButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(healthListFragment)
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        return todayDate
    }

    private fun showAlertText() = with(binding){
        alertLinearLayout.isVisible = true
    }

    private fun hideAlertText() = with(binding){
        alertLinearLayout.isVisible = false
    }

    private fun showView() = with(binding) {
        view.isVisible = true
    }

    private fun hideView() = with(binding) {
        view.isVisible = false
    }

    private fun showProgress() = with(binding) {
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() = with(binding) {
        binding.progressBar.isVisible = false
    }

}