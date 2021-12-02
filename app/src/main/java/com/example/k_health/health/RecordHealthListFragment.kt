package com.example.k_health.health

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.Repository.userId
import com.example.k_health.databinding.FragmentRecordHealthlistBinding
import com.example.k_health.model.HealthRecord
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RecordHealthListFragment : BottomSheetDialogFragment() {

    private var binding: FragmentRecordHealthlistBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private var recordHealthList: ArrayList<HealthRecord> = arrayListOf()
    private var recordHealthListAdapter = RecordHealthListAdapter(healthRecordData = recordHealthList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record_healthlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recordHealthlistBinding = FragmentRecordHealthlistBinding.bind(view)
        binding = recordHealthlistBinding


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

    private fun initViews() {
        binding!!.healthlistNameTextView.text = arguments?.getString("name")
        binding!!.healthlistEngnameTextView.text = arguments?.getString("engName")

        binding!!.submitButton.setOnClickListener {
            Toast.makeText(requireContext(), "클릭되었습니다.", Toast.LENGTH_LONG).show()
        }

        binding!!.exitImageButton.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        recordHealthList.add(HealthRecord("1set", "0", "0"))
        binding!!.recyclerView.apply {
            adapter = recordHealthListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun addRecordView() {
        binding!!.addSetButton.setOnClickListener {
            recordHealthList.add(HealthRecord("${recordHealthList.size+1}set", "0", "0"))
            recordHealthListAdapter.notifyDataSetChanged()
        }
    }

    private fun uploadHealthRecord() {
        val today = timeGenerator()
        val healthName: String = arguments?.getString("name") ?: "null"
        val healthRecordData = mutableMapOf<String, Any>()
        healthRecordData["set"] = recordHealthList[0].set
        healthRecordData["weight"] = recordHealthList[0].weight
        healthRecordData["count"] = recordHealthList[0].count
        healthRecordData["today"] = today

        binding!!.submitButton.setOnClickListener {
            db.collection("Users/${userId}/healthRecord")
                .document(healthName)
                .set(healthRecordData)
                .addOnSuccessListener {
                    Log.d("record","success")
                    dismiss()
                }
        }
    }


    private fun timeGenerator(): String {
        var now = LocalDate.now()
        var todayNow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        return todayNow
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        val layoutParams = bottomSheet!!.layoutParams
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
}