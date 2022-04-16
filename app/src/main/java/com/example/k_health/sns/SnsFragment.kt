package com.example.k_health.sns

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.k_health.DBKey
import com.example.k_health.MainActivity
import com.example.k_health.R
import com.example.k_health.databinding.FragmentSnsBinding
import com.example.k_health.health.TimeInterface
import com.example.k_health.home.HomeFragment
import com.example.k_health.sns.adapter.SnsArticleListAdapter
import com.example.k_health.sns.model.SNS
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SnsFragment : Fragment(R.layout.fragment_sns), TimeInterface {

    companion object {
        const val TAG = "SnsFragment"
    }

    private var _binding: FragmentSnsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var snsArticleList = mutableListOf<SNS>()
    private val snsArticleListAdapter = SnsArticleListAdapter()
    private val today = timeGenerator()
    private val scope = MainScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSnsBinding.bind(view)

        setToolbar()
        initSnsArticleRecyclerView()
        fetchSnsArticleItems()
        refreshBoard()


    }

    private fun setToolbar() = with(binding) {
        val snsWriteFragment = SnsWriteFragment()
        toolbar.inflateMenu(R.menu.toolbar_sns)

        // 프래그먼트가 메뉴 관련 콜백을 수신하려 한다고 시스템에 알림
        // 메뉴 관련 이벤트(생성, 클릭 등)가 발생하면 이벤트 처리 메서드가 먼저 활동에서 호출된 후 프래그먼트에서 호출
        // setHasOptionsMenu(true)
        toolbar.title = "Together"

        // 액션버튼 클릭 했을 때 이벤트 처리
        toolbar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_write -> {
                    //글쓰기 버튼 눌렀을 때
                    (activity as MainActivity).replaceFragment(snsWriteFragment)
                    Log.d(TAG, "글쓰기 버튼 클릭")
                    true
                }
                R.id.action_refresh -> {
                    //새로고침 버튼 눌렀을 때
                    fetchSnsArticleItems()
                    showSnackBar("새로고침이 완료되었습니다.")
                    Log.d(TAG, "새로고침 버튼 클릭")
                    true
                }
                else -> false
            }
        }
    }


    private fun initSnsArticleRecyclerView() = with(binding) {
        snsArticleRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = snsArticleListAdapter
        }
    }

    private fun fetchSnsArticleItems() = with(binding) {
        snsArticleList.clear()
        CoroutineScope(Dispatchers.IO).launch {

            runBlocking {
                snsArticleList = getSnsArticleItems()
                Log.d(TAG, "runblocking: $snsArticleList")
            }

            scope.launch {

                if (!snsArticleList.isEmpty()) {
                        hideAlertText()
                    showRecyclerView()
                    (snsArticleRecyclerView.adapter as SnsArticleListAdapter).apply {
                        Log.d(TAG, "in adapter: $snsArticleList")
                        submitList(snsArticleList.reversed())
                    }
                } else {
                    hideRecyclerView()
                    showAlertText()
                }
            }
        }
    }


    private suspend fun getSnsArticleItems(): MutableList<SNS> {
        return try {
            db.collection(DBKey.COLLECTION_NAME_SNS)
                .document(today)
                .collection(today)
                .get()
                .addOnSuccessListener { snapshot ->
                    for (i in snapshot.documents.indices) {
                        val snsArticleItem = snapshot.documents[i].toObject(SNS::class.java)
                        snsArticleList.add(snsArticleItem!!)
                    }
                    Log.d(TAG, "get: $snsArticleList")
                }.await()
            snsArticleList
        } catch (e: FirebaseFirestoreException) {
            snsArticleList
        }
    }

    private fun refreshBoard() = with(binding) {
        refreshLayout.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                fetchSnsArticleItems()
                refreshLayout.isRefreshing = false // 작업이 끝난 후 로딩 표시 해제
                showSnackBar("새로고침이 완료되었습니다.")
            }
        })
    }

    private fun showSnackBar(alertText: String) {
        Snackbar.make(requireView(), alertText, Snackbar.LENGTH_INDEFINITE)
            .setAction("확인", object : View.OnClickListener {
                override fun onClick(v: View?) {

                }
            })
            .show()
    }

    // 메뉴를 앱 바의 옵션 메뉴에 병합하려면 프래그먼트에서 onCreateOptionsMenu()를 재정의
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_sns, menu)
    }

    override fun timeGenerator(): String {
        return super.timeGenerator()
    }

    private fun showAlertText() = with(binding) {
        alertLinearLayout.visibility = View.VISIBLE
    }

    private fun hideAlertText() = with(binding) {
        alertLinearLayout.visibility = View.GONE
    }

    private fun showRecyclerView() = with(binding) {
        snsArticleRecyclerView.visibility = View.VISIBLE
    }

    private fun hideRecyclerView() = with(binding) {
        snsArticleRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
        Log.d(TAG, "onDestroyView")
    }

}