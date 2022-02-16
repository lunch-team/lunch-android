package com.lunchteam.lunch.menu

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivityLunchMenuDetailBinding
import com.lunchteam.lunch.util.MyApplication
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LunchMenuDetail : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var menuId = 0
    private var list: ArrayList<JSONObject>? = null
    private val CALLBACK_WRITE_REVIEW = 100

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

        getMenuDetail(menuId)

        binding.btReviewDetail.setOnClickListener {
            if (MyApplication.prefs.getString("memberId", "") != "") {
                val intent = Intent(mContext, LunchWriteReview::class.java)
                intent.putExtra("id", menuId)
                intent.putExtra("name", binding.tvRestaurantName.text.toString())
                startActivityForResult(intent, CALLBACK_WRITE_REVIEW)
            } else {
                Toast.makeText(mContext, "리뷰를 작성하려면 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }

        binding.btMore.setOnClickListener {
            var items = arrayOf("삭제하기", "수정하기", "방문추가")
            var dialog: AlertDialog.Builder = AlertDialog.Builder(this)
            dialog.setTitle("test")
                    .setItems(items) { dialog, which ->
                        when (which) {
                            0 -> // 삭제하기
                                Toast.makeText(mContext, "${items[which]} is Selected", Toast.LENGTH_SHORT).show()
                            1 -> // 수정하기
                                Toast.makeText(mContext, "${items[which]} is Selected", Toast.LENGTH_SHORT).show()
                            2 -> {// 방문추가
                                val values = JSONObject()
                                try {
                                    values.put("id", menuId)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                var url = server_url + resources.getString(R.string.visit_menu_path)
                                var moreMenuTask: MoreMenuTask = MoreMenuTask(url, values)
                                moreMenuTask.execute()

                                showProgress(true)
                                Toast.makeText(mContext, "${items[which]} is Selected", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .show()


        }
    }

    private fun getMenuDetail(id: Int?) {
        if (id == 0) {
            Toast.makeText(mContext, "메뉴 ID값이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return;
        }
        val values = JSONObject()
        try {
            values.put("id", id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var url = server_url + path
        val networkTask: GetReviewTask = GetReviewTask(url, values)
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


    inner class GetReviewTask : AsyncTask<Void, Void, String> {
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
                // 메뉴 상세내용 셋팅
                setMenuDetail(menuDetail, menuReview.length())

                // 리뷰 셋팅
                setMenuReview(list!!)


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            showProgress(false)
        }

        // 메뉴 상세내용 셋팅 함수
        private fun setMenuDetail(menuDetail: JSONObject, reviewCnt: Int) {
            try {
                binding.tvRestaurantName.text = menuDetail.getString("name")
                binding.tvAddress.text = menuDetail.getString("location")
                val getStar = menuDetail.getDouble("star")
                var star = 0.0
                if (5 < getStar)
                    star = getStar / 2
                binding.tvReviewStarCnt.text = String.format("%.1f", star)
                binding.tvReviewTopCnt.text = reviewCnt.toString()
                binding.tvReviewLength.text = reviewCnt.toString() + "개"

            } catch (e: Exception) {

            }
        }

        // 메뉴 리뷰 셋팅 함수
        private fun setMenuReview(menuReview: ArrayList<JSONObject>) {
            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            binding.rvReview.layoutManager = LinearLayoutManager(mContext)

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            val url = resources.getString(R.string.server_url) + resources.getString(R.string.get_review_img_path)
            val remove_url = resources.getString(R.string.server_url) + resources.getString(R.string.remove_review_path)
            val adapter = MenuReviewAdapter(menuReview, mContext, url, remove_url)

            binding.rvReview.adapter = adapter
        }
    }


    inner class MoreMenuTask : AsyncTask<Void, Void, String> {
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
            lunchLog(s.toString())
            showProgress(false)
        }

    }

    // Activity Result 가 있는 경우 실행되는 콜백함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CALLBACK_WRITE_REVIEW -> {
                    val id: Int? = data?.getIntExtra("id", 0)
                    getMenuDetail(id)
                }
            }
        }
    }


}