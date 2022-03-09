package com.example.k_health.health.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.k_health.databinding.ItemHealthsetBinding
import com.example.k_health.health.model.HealthRecord

class RecordHealthListAdapter(val healthRecordData: ArrayList<HealthRecord>) :
    RecyclerView.Adapter<RecordHealthListAdapter.ViewHolder>() {

    companion object {
        const val TAG = "RecordHealthListAdapter"
    }

    val weightRecordedData = mutableMapOf<String, String>()
    val countRecordedData = mutableMapOf<String, String>()
    val recordedData = mutableListOf<HealthRecord>()

    fun getAll(): List<HealthRecord> {
        val weightList = weightRecordedData.values.toList()
        val countList = countRecordedData.values.toList()

        for (i in weightList.indices) {
            recordedData.add(HealthRecord((i + 1).toString(), weightList[i], countList[i]))
        }

        return recordedData.distinct()
    }

    inner class ViewHolder(private val binding: ItemHealthsetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(healthRecord: HealthRecord) = with(binding) {
            val pos = adapterPosition

            setTextView.text = healthRecord.set
            weightEditText.setText(healthRecord.weight)
            countEditText.setText(healthRecord.count)

            // 첫 번째 운동세트는 못지우게 삭제버튼 GONE처리
            if (pos == 0) deleteImageButton.visibility =
                View.GONE else deleteImageButton.visibility = View.VISIBLE

            deleteImageButton.setOnClickListener {
                healthRecordData.removeAt(pos)
                notifyDataSetChanged()
            }

            weightEditText.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        weightRecordedData.put((pos + 1).toString(), p0.toString())
                        Log.d(TAG, "weight: ${weightRecordedData}")
                    }

                    override fun afterTextChanged(p0: Editable?) {}
                })
                setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        this.hint = null
                        this.text = null
                    }
                    return@setOnTouchListener false
                }
            }

            countEditText.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        countRecordedData.put((pos + 1).toString(), p0.toString())
                        Log.d(TAG, "count: $countRecordedData")
                    }

                    override fun afterTextChanged(p0: Editable?) {}

                })

                setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        this.hint = null
                        this.text = null
                    }
                    return@setOnTouchListener false
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthsetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(healthRecordData[position])
        Log.d("tag", "position : $position")
    }

    override fun getItemCount() = healthRecordData.size


}
