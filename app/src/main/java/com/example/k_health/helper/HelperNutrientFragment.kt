package com.example.k_health.helper

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHelperNutrientBinding
import com.example.k_health.model.Nutrient
import com.google.firebase.firestore.FirebaseFirestore

class HelperNutrientFragment: Fragment(R.layout.fragment_helper_nutrient) {

    private var binding: FragmentHelperNutrientBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private val nutrientList: ArrayList<Nutrient> = arrayListOf()
    private val helperNutrientAdapter = HelperNutrientAdapter(nutrientList)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHelperNutrientBinding = FragmentHelperNutrientBinding.bind(view)
        binding = fragmentHelperNutrientBinding

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding!!.nutrientRecyclerView.apply {
            adapter = helperNutrientAdapter
            layoutManager = LinearLayoutManager(context)
        }

        db.collection(DBKey.COLLECTION_NAME_NUTRIENT)
            .addSnapshotListener {querySnapshot, firebaseFirestoreException ->
                for (snapshot in querySnapshot!!.documents) {
                    val nutrientitem = snapshot.toObject(Nutrient::class.java)

                    nutrientList.add(nutrientitem!!)
                }

                helperNutrientAdapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}