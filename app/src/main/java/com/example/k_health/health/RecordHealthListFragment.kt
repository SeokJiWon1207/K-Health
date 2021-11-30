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
import androidx.fragment.app.setFragmentResultListener
import com.example.k_health.databinding.FragmentRecordHealthlistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecordHealthListFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRecordHealthlistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            binding.healthlistNameTextView.text = bundle.getString("name")
            Log.d("TAG", "name :${bundle.getString("name")}")
            binding.healthlistEngnameTextView.text = bundle.getString("engName")
        }
        // initViews()



    }

    private fun initViews() {
        // var offsetFromTop =
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet!!.layoutParams.height = getBottomSheetDialogDefaultHeight()

        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.isFitToContents = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        setFragmentResultListener("requestKey") { requestKey, bundle ->
            binding.healthlistNameTextView.text = bundle.getString("name")
            Log.d("TAG", "name :${bundle.getString("name")}")
            binding.healthlistEngnameTextView.text = bundle.getString("engName")
        }

        binding.exitImageButton.setOnClickListener {
            dismiss()
        }
    }


    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        //id = com.google.android.material.R.id.design_bottom_sheet for Material Components
        //id = android.support.design.R.id.design_bottom_sheet for support librares
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