package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.DBKey
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthlistBinding
import com.example.k_health.model.HealthList
import com.google.firebase.firestore.FirebaseFirestore

class HealthListFragment : Fragment(R.layout.fragment_healthlist) {

    private var binding: FragmentHealthlistBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private var healthlist: ArrayList<HealthList> = arrayListOf()
    private var healthListAdapter = HealthListAdapter(healthdata = healthlist)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthListBinding = FragmentHealthlistBinding.bind(view)
        binding = fragmentHealthListBinding

        db.collection("덤벨")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(HealthList::class.java)

                    healthlist.add(item!!)
                    Log.d("TAG", "${item}")
                }
                healthListAdapter.notifyDataSetChanged()
                Log.d("TAG", "${healthlist.size}")
                Log.d("TAG", "${firebaseFirestoreException}")

            }

        initTab()
        initRecyclerView()
    }

    private fun initTab() {

        val tabMainList = listOf("가슴", "등", "어깨", "하체", "복근", "삼두", "이두", "전신", "코어")
        val tabSubList = listOf("머신", "덤벨", "바벨", "케틀벨", "케이블", "스미스머신")

        for (i in tabMainList.indices) {
            binding?.mainTablayout!!.addTab(
                binding?.mainTablayout!!.newTab().setText(
                    tabMainList[i]
                )
            )
        }

        for (i in tabSubList.indices) {
            binding?.subTablayout!!.addTab(
                binding?.subTablayout!!.newTab().setText(
                    tabSubList[i]
                )
            )
        }
    }

    private fun initRecyclerView() {
        binding!!.recyclerView.apply {
            adapter = healthListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

}
