package com.example.k_health.food

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.k_health.R
import com.example.k_health.Repository
import com.example.k_health.Url
import com.example.k_health.databinding.FragmentFoodSearchBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class FoodSearchFragment: Fragment(R.layout.fragment_food_search) {

    companion object {
        const val TAG = "network"
    }

    private var binding: FragmentFoodSearchBinding? = null
    private val foodListAdapter = FoodListAdapter()
    private val scope = MainScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val FragmentFoodSearchBinding = FragmentFoodSearchBinding.bind(view)
        binding = FragmentFoodSearchBinding

        initFoodRecyclerView()
        fetchFoodItems()

        /*val thread = NetworkThread()
        thread.start()
        thread.join()*/


    }

    private fun initFoodRecyclerView() {
        binding!!.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodListAdapter
        }
    }

    // Retrofit 1-4) FoodApiService의 Method 사용으로 받아온 데이터를 RecyclerView에 뿌려주기
    private fun fetchFoodItems(query: String? = null) = scope.launch {
        try {
            Repository.getFoodItems(query)?.let {
                (binding!!.foodRecyclerView.adapter as? FoodListAdapter)?.apply {
                    Log.d(TAG,"items : ${it}")
                    // submitList(it.items)
                }
            }
        } catch (exception: Exception) {
            Log.d(TAG,"exception : $exception")
        }
    }

    // 네트워크를 이용할 때는 쓰레드를 사용해서 접근해야 함
    /*inner class NetworkThread: Thread(){
        override fun run() {
            val url = URL(Url.URL)
            val conn = url.openConnection()
            val input = conn.getInputStream()
            val isr = InputStreamReader(input)
            // br: 라인 단위로 데이터를 읽어오기 위해서 만듦
            val br = BufferedReader(isr)

            // Json 문서는 일단 문자열로 데이터를 모두 읽어온 후, Json에 관련된 객체를 만들어서 데이터를 가져옴
            var str: String? = null
            val buf = StringBuffer()

            do{
                str = br.readLine()

                if(str!=null){
                    buf.append(str)
                }
            }while (str!=null)

            // 전체가 객체로 묶여있기 때문에 객체형태로 가져옴
            val root = JSONObject(buf.toString())
            val response = root.getJSONObject("body")
            val items = response.getJSONArray("items") // 객체 안에 있는 items이라는 이름의 리스트를 가져옴

            Log.d(TAG,"item : $items")

            for (i in 0 until 1) {
                val jObject = items.getJSONObject(i)
                println(JSON_Parse(jObject,"DESC_KOR"))
                println(JSON_Parse(jObject,"NUTR_CONT1"))
                println(JSON_Parse(jObject,"NUTR_CONT2"))
                println(JSON_Parse(jObject,"NUTR_CONT3"))
                println(JSON_Parse(jObject,"NUTR_CONT4"))


            }
        }

        // 함수를 통해 데이터를 불러온다.
        fun JSON_Parse(obj:JSONObject, data : String): String {

            // 원하는 정보를 불러와 리턴받고 없는 정보는 캐치하여 "없습니다."로 리턴받는다.
            return try {

                obj.getString(data)

            } catch (e: Exception) {
                "없습니다."
            }
        }
    }*/


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        scope.cancel()
    }

}