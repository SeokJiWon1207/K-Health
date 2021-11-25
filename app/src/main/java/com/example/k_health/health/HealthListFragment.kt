package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthlistBinding
import com.example.k_health.model.HealthList
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class HealthListFragment : Fragment(R.layout.fragment_healthlist) {

    private var binding: FragmentHealthlistBinding? = null
    private val db = FirebaseFirestore.getInstance()
    private var healthlist: ArrayList<HealthList> = arrayListOf()
    private var healthListAdapter = HealthListAdapter(healthdata = healthlist)

    val tabMainList = listOf("가슴", "등", "어깨", "하체", "복근", "삼두", "이두", "전신", "코어")
    val tabSubList = listOf("바벨", "덤벨", "머신", "케틀벨", "케이블", "스미스머신")

    var mainPosition: Int = 0
    var subPosition: Int = 0

    init {
        db.collection("Health/가슴/바벨")
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHealthListBinding = FragmentHealthlistBinding.bind(view)
        binding = fragmentHealthListBinding


        initTab()
        initRecyclerView()

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

        setDualTabs(binding!!.mainTablayout, binding!!.subTablayout, tabMainList, tabSubList)

    }

    private fun initRecyclerView() {
        binding!!.recyclerView.apply {
            adapter = healthListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }



    private fun setDualTabs(
        mainTab: TabLayout, subTab: TabLayout, mainText: List<String>, subText: List<String>
    ) {

        mainTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(maintab: TabLayout.Tab?) {
                when (maintab?.position) {

                    maintab?.position -> db.collection("Health/${mainText[maintab?.position!!]}/${subText[subPosition]}")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthlist.clear()
                            Log.d("TAG", "mainText: ${mainText[maintab?.position!!]}")
                            for (snapshot in querySnapshot!!.documents) {
                                var healthitem = snapshot.toObject(HealthList::class.java)

                                healthlist.add(healthitem!!)
                            }

                            mainPosition = maintab?.position!!
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

        subTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(subtab: TabLayout.Tab?) {
                when (subtab?.position) {

                    subtab?.position -> db.collection("Health/${mainText[mainPosition]}/${subText[subtab?.position!!]}")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthlist.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                var healthitem = snapshot.toObject(HealthList::class.java)

                                healthlist.add(healthitem!!)
                            }
                            subPosition = subtab?.position!!
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


    }

}
