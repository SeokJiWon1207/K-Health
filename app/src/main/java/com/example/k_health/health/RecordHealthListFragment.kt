package com.example.k_health.health

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.GlobalApplication
import com.example.k_health.R
import com.example.k_health.databinding.FragmentRecordHealthlistBinding
import com.example.k_health.health.adapter.RecordHealthListAdapter
import com.example.k_health.health.model.HealthRecord
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordHealthListFragment : BottomSheetDialogFragment(),TimeInterface {

    companion object {
        const val TAG = "RecordHealthListFragment"
    }

    private var _binding: FragmentRecordHealthlistBinding? = null
    private val binding get() = _binding!!
    private val userId = Firebase.auth.currentUser?.uid.orEmpty()
    private val db = FirebaseFirestore.getInstance()
    private var recordHealthList = arrayListOf<HealthRecord>()
    private var recordHealthListAdapter = RecordHealthListAdapter(recordHealthList)
    private val today = timeGenerator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_healthlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRecordHealthlistBinding.bind(view)

        initBottomSheetDialogFragment()
        initViews()
        initRecyclerView()
        addRecordView()
        uploadHealthRecord()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }

        return dialog
    }

    private fun initBottomSheetDialogFragment() {
        // 팝업 생성 시 전체화면으로 띄우기
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // 드래그해도 팝업이 종료되지 않도록
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun initViews() = with(binding) {

        healthlistNameTextView.text = arguments?.getString("name")
        healthlistEngnameTextView.text = arguments?.getString("engName")

        selectedHealthDateTextView.text =
            GlobalApplication.prefs.getString("selectedHealthDate",timeGenerator())

        submitButton.setOnClickListener {
            Toast.makeText(requireContext(), "클릭되었습니다.", Toast.LENGTH_LONG).show()
        }

        exitImageButton.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        recordHealthList.add(HealthRecord("1set","0","0"))
        binding.recyclerView.apply {
            adapter = recordHealthListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun addRecordView() {
        binding.addSetButton.setOnClickListener {
            recordHealthList.add(HealthRecord("${recordHealthList.size + 1}set","0","0"))
            recordHealthListAdapter.notifyDataSetChanged()
        }
    }

    private fun uploadHealthRecord() = with(binding) {
        val selectedHealthDate = GlobalApplication.prefs.getString("selectedHealthDate",today)
        val todayHealthSet = GlobalApplication.prefs.getStringSet(selectedHealthDate, mutableSetOf("", ""))
        todayHealthSet.remove("")
        val healthName: String = arguments?.getString("name") ?: "null"

        submitButton.setOnClickListener {
            val userHealthRecordedData = recordHealthListAdapter.getAll()
            for (i in userHealthRecordedData.indices) {
                db.collection(DBKey.COLLECTION_NAME_USERS)
                    .document(userId)
                    .collection(DBKey.COLLECTION_NAME_HEALTHRECORD) // 헬스기록보관
                    .document(selectedHealthDate) // 당일 날짜
                    .collection(healthName) // 현재 클릭한 운동의 이름
                    .document(userHealthRecordedData[i].set) // 첫 번째 세트
                    .set(userHealthRecordedData[i]) // 운동 데이터
                    .addOnSuccessListener {
                        dismiss()
                    }
                    .addOnFailureListener { error ->
                        Log.d(TAG, "error : $error")
                    }
                todayHealthSet!!.add(healthName)
                Log.d(TAG,"after: $todayHealthSet")
                GlobalApplication.prefs.setStringSet(selectedHealthDate, todayHealthSet)
            }
        }
    }

    override fun timeGenerator(): String {
        val now = LocalDate.now()
        val todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        return todayNow
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int = getWindowHeight() * 85 / 100

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}