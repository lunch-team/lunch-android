package com.lunchteam.lunch.menu

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivityLunchMenuDetailBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LunchMenuDetail : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var menuId = 0
    private var list: ArrayList<JSONObject>? = null

    private lateinit var binding: ActivityLunchMenuDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        path = resources.getString(R.string.get_Menu_detail_path)

        // 클릭한 식당 id 값 가져오기
        menuId = intent.getIntExtra("id", 0)

        // toolbar 셋팅
        initToolbar("메뉴 상세")
        mContext = this

        getMenuDetail(menuId)
    }

    private fun getMenuDetail(id: Int) {
        val values = JSONObject()
        try {
            values.put("id", id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var url = server_url + path
        val networkTask: NetworkTask = NetworkTask(url, values)
        networkTask.execute()
        showProgress(true)

    }

    // 프로그레스바 함수
    fun showProgress(isShow: Boolean) {
        if (isShow)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }


    inner class NetworkTask : AsyncTask<Void, Void, String> {
        private var url: String
        private var values: ContentValues? = null
        private var valuesJson: JSONObject? = null

        constructor(url: String, values: ContentValues?) {
            this.url = url
            this.values = values
        }

        constructor(url: String, values: JSONObject?) {
            this.url = url
            valuesJson = values
        }

        override fun doInBackground(vararg p0: Void): String? {
            val result: String? // 요청 결과를 저장할 변수.
            val requestHttpURLConnection = RequestHttpURLConnection()

            result = if (values != null)
                requestHttpURLConnection.request(url, values!!) // 해당 URL로 부터 결과물을 얻어온다.
            else
                requestHttpURLConnection.request(url, valuesJson)
            return result
        }

        public override fun onPostExecute(s: String?) {
            super.onPostExecute(s) //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            val count: Int
            val menudata: JSONObject
            val menuReview: JSONArray
            val menuDetail: JSONObject

            list = ArrayList()
            try {
                val data = JSONObject(s)
                count = data.getInt("count")
                menudata = data.getJSONObject("data")
                menuDetail = menudata.getJSONObject("menuDetail")
                menuReview = menudata.getJSONArray("menuReview")

                for (i in 0 until menuReview.length()) {
                    val menu = menuReview.getJSONObject(i)
                    list!!.add(menu)
                }


                setMenuDetail(menuDetail, menuReview.length())


                //리뷰


                setMenuReview(list!!)


            } catch (e: JSONException) {
                e.printStackTrace()
            }

/*
            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            binding.rvMenu.layoutManager = LinearLayoutManager(mContext)

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            val adapter = MenuAdapter(list!!, mContext)


            binding.rvMenu.adapter = adapter*/
            showProgress(false)
        }

        private fun setMenuDetail(menuDetail: JSONObject, reviewCnt: Int) {
            try {
                binding.tvRestaurantName.text = menuDetail.getString("name")
                binding.tvAddress.text = menuDetail.getString("location")
                binding.tvReviewStarCnt.text = menuDetail.getString("star")
                binding.tvReviewTopCnt.text = reviewCnt.toString()
                binding.tvReviewLength.text = reviewCnt.toString() + "개"

            } catch (e: Exception) {

            }
        }

        private fun setMenuReview(menuReview: ArrayList<JSONObject>) {
            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            binding.rvReview.layoutManager = LinearLayoutManager(mContext)

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            val url = resources.getString(R.string.server_url) + resources.getString(R.string.get_review_img_path)
            val adapter = MenuReviewAdapter(menuReview, mContext, url)


            binding.rvReview.adapter = adapter
        }
    }


}