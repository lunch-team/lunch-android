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
import com.lunchteam.lunch.databinding.ActivityLunchVisitMenuListBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LunchVisitMenuList : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var ORDER = "DESC" //DESC : 내림차순 , ASC : 오름차순
    private var list: ArrayList<JSONObject>? = null

    private lateinit var binding: ActivityLunchVisitMenuListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchVisitMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        path = resources.getString(R.string.get_visit_menulist_path)

        // toolbar 셋팅
        initToolbar("방문 기록")

        setVisitMenuList()


    }

    private fun setVisitMenuList() {
        var values = JSONObject()
        try {
            values.put("order", ORDER)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var url = server_url + path
        val networkTask: NetworkTask = NetworkTask(url, values)
        networkTask.execute()

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
            val menulist: JSONArray
            val count: Int
            list = ArrayList()
            try {
                val data = JSONObject(s)
                count = data.getInt("count")
                menulist = JSONArray(data.getString("data"))
                for (i in 0 until count) {
                    val menu = menulist.getJSONObject(i)
                    list!!.add(menu)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            binding.rvMenu.layoutManager = LinearLayoutManager(mContext)

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            val deleteUrl = server_url + resources.getString(R.string.delete_menu_log_path)
            val adapter = VisitMenuListAdapter(list!!, mContext, deleteUrl)


            binding.rvMenu.adapter = adapter
            showProgress(false)
        }
    }

    // 프로그레스바 함수
    fun showProgress(isShow: Boolean) {
        if (isShow)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }
}