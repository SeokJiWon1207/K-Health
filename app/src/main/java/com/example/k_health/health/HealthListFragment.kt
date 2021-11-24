package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.k_health.DBKey
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthlistBinding
import com.example.k_health.databinding.ItemHealthlistBinding
import com.example.k_health.model.HealthList
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HealthListFragment : Fragment(R.layout.fragment_healthlist) {

    private var binding: FragmentHealthlistBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private var healthlist: ArrayList<HealthList> = arrayListOf()
    private var healthListAdapter = HealthListAdapter(healthdata = healthlist)

    val tabMainList = listOf("가슴", "등", "어깨", "하체", "복근", "삼두", "이두", "전신", "코어")
    val tabSubList = listOf("바벨", "덤벨", "머신", "케틀벨", "케이블", "스미스머신")

    val tabList = arrayListOf(tabMainList, tabSubList)

    /*init {
        db.collection("Health/가슴/바벨")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                healthlist.clear()
                for (snapshot in querySnapshot!!.documents) {
                    var healthitem = snapshot.toObject(HealthList::class.java)
                    storage.getReferenceFromUrl("gs://khealth-942c7.appspot.com/health/가슴/바벨")
                        .child("${healthitem!!.name}.JPG").downloadUrl.addOnCompleteListener {
                            Log.d("TAG","name : ${healthitem.name}")
                            if (it.isSuccessful) {
                                Log.d("TAG","it.result : ${it.result}")
                                healthitem.imageUrl = it.result.toString()
                            }
                        }
                        .addOnFailureListener { error ->
                            Log.d("TAG","error : $error")
                        }
                    healthlist.add(healthitem!!)
                }

                healthListAdapter.notifyDataSetChanged()
                Log.d("TAG", "${firebaseFirestoreException}")

            }
    }*/


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthListBinding = FragmentHealthlistBinding.bind(view)
        binding = fragmentHealthListBinding


        initTab()
        initRecyclerView()


        /*db.collection("Health/가슴/바벨")
            .get()
            .addOnSuccessListener { document ->
                healthlist[0] = document["name"].toString()
                binding?.userWeightTextView?.text = document["userWeight"].toString()
                binding?.userMuscleTextView?.text = document["userMuscle"].toString()
                binding?.userFatTextView?.text = document["userFat"].toString()

            }
            .addOnFailureListener {

            }*/


    }

    private fun initTab() {

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

        setDualTabs(binding!!.mainTablayout, binding!!.subTablayout)

    }

    private fun initRecyclerView() {
        binding!!.recyclerView.apply {
            adapter = healthListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /*private fun setTabRecyclerView() {

        binding!!.mainTablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> db.collection("Health/가슴/바벨")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthlist.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                var healthitem = snapshot.toObject(HealthList::class.java)

                                healthlist.add(healthitem!!)
                            }

                            healthListAdapter.notifyDataSetChanged()
                            Log.d("TAG", "${firebaseFirestoreException}")

                        }


                    1 -> db.collection("Health/등/머신")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthlist.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                var healthitem = snapshot.toObject(HealthList::class.java)

                                healthlist.add(healthitem!!)
                            }

                            healthListAdapter.notifyDataSetChanged()
                            Log.d("TAG", "${firebaseFirestoreException}")

                        }

                    2 -> db.collection("Health/어깨/머신")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthlist.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                var healthitem = snapshot.toObject(HealthList::class.java)

                                healthlist.add(healthitem!!)
                            }

                            healthListAdapter.notifyDataSetChanged()
                            Log.d("TAG", "${firebaseFirestoreException}")

                        }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "unselect")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "Reselect")
            }

        })

    }*/

    // TODO 이중탭 클릭 구현하기
    private fun setDualTabs(mainTab: TabLayout, subTab: TabLayout) {
        var mainTabText: String = "가슴"
        var subTabText: String = "바벨"

        var list = mutableListOf(mainTabText,subTabText)

        mainTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text.toString()) {
                    tab?.text.toString() -> mainTabText = tab?.text.toString()
                    tab?.text -> Log.d("TAG","tab.text: ${tab?.text.toString()}")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "unselect")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "Reselect")
            }

        })

        subTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    tab?.text -> subTabText = tab?.text.toString()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "unselect")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("TAG", "Reselect")
            }

        })

        db.collection("Health/${mainTabText}/${subTabText}")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                healthlist.clear()
                Log.d("TAG","${mainTabText}/${subTabText}")
                for (snapshot in querySnapshot!!.documents) {
                    var healthitem = snapshot.toObject(HealthList::class.java)

                    healthlist.add(healthitem!!)
                }

                healthListAdapter.notifyDataSetChanged()
                Log.d("TAG", "${firebaseFirestoreException}")

            }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
