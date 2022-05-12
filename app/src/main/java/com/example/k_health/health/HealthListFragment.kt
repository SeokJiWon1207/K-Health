package com.example.k_health.health

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.R
import com.example.k_health.databinding.FragmentHealthlistBinding
import com.example.k_health.health.adapter.HealthListAdapter
import com.example.k_health.health.model.HealthList
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class HealthListFragment : Fragment(R.layout.fragment_healthlist) {

    private var _binding: FragmentHealthlistBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var healthList: ArrayList<HealthList> = arrayListOf()
    private var healthListAdapter = HealthListAdapter(healthData = healthList)

    private val tabMainList = listOf("가슴", "등", "어깨", "하체", "복근", "삼두", "이두", "전신")
    private val tabSubList = listOf("바벨", "덤벨", "머신", "케틀벨", "케이블", "스미스머신")

    private var mainPosition: Int = 0 // 메인탭의 포지션
    private var subPosition: Int = 0 // 서브탭의 포지션


    init {
        db.collection("Health/가슴/바벨")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                healthList.clear()
                for (snapshot in querySnapshot!!.documents) {
                    val healthItem = snapshot.toObject(HealthList::class.java)

                    healthList.add(healthItem!!)
                }

                healthListAdapter.notifyDataSetChanged()
                Log.d("TAG", "${firebaseFirestoreException}")

            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHealthlistBinding.bind(view)

        initTab()
        initRecyclerView()
        clickAdapter()


    }

    private fun initTab() = with(binding) {

        for (i in tabMainList.indices) {
            mainTabLayout.addTab(
                mainTabLayout.newTab().setText(
                    tabMainList[i]
                )
            )
        }

        for (i in tabSubList.indices) {
            subTabLayout.addTab(
                subTabLayout.newTab().setText(
                    tabSubList[i]
                )
            )
        }

        setDualTabs(binding.mainTabLayout, binding.subTabLayout, tabMainList, tabSubList)

    }

    private fun initRecyclerView() = with(binding) {
        healthlistRecyclerView.apply {
            adapter = healthListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun clickAdapter() {
        healthListAdapter.setOnItemClickListener(object : HealthListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: HealthList, pos: Int) {
                val bundle = Bundle()

                bundle.putString("name", data.name)
                bundle.putString("engName", data.engName)

                val recordHealthListFragment = RecordHealthListFragment()

                recordHealthListFragment.arguments = bundle

                recordHealthListFragment.show(childFragmentManager, "record")
            }

        })
    }


    private fun setDualTabs(
        mainTab: TabLayout, subTab: TabLayout, mainText: List<String>, subText: List<String>
    ) {

        mainTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(maintab: TabLayout.Tab?) {
                when (maintab?.position) {

                    maintab?.position -> db.collection("Health/${mainText[maintab?.position!!]}/${subText[subPosition]}")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthList.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                val healthitem = snapshot.toObject(HealthList::class.java)

                                healthList.add(healthitem!!)
                            }

                            healthListAdapter.notifyDataSetChanged()

                            mainPosition = maintab.position
                        }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        subTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(subtab: TabLayout.Tab?) {
                when (subtab?.position) {

                    subtab?.position -> db.collection("Health/${mainText[mainPosition]}/${subText[subtab?.position!!]}")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            healthList.clear()
                            for (snapshot in querySnapshot!!.documents) {
                                val healthitem = snapshot.toObject(HealthList::class.java)

                                healthList.add(healthitem!!)
                            }

                            if (healthList.isEmpty()) {
                                hideRecyclerView()
                                showUpdateView()
                            } else {
                                showRecyclerView()
                                hideUpdateView()
                                healthListAdapter.notifyDataSetChanged()
                            }
                            subPosition = subtab.position
                        }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

    }

    private fun showUpdateView() = with(binding) {
        updateView.visibility = View.VISIBLE
    }

    private fun hideUpdateView() = with(binding) {
        updateView.visibility = View.GONE
    }

    private fun showRecyclerView() = with(binding) {
        healthlistRecyclerView.visibility = View.VISIBLE
    }

    private fun hideRecyclerView() = with(binding) {
        healthlistRecyclerView.visibility = View.GONE
    }

}
