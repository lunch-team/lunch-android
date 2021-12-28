package com.lunchteam.lunch.menu

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivityLunchAllMenuListBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LunchAllMenuList : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var list: ArrayList<JSONObject>? = null
    private var str_order: String? = null
    private var str_menuType: String? = null
    private var str_orderType: String? = null


    private lateinit var binding: ActivityLunchAllMenuListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchAllMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = resources.getString(R.string.menu_list_path)

        // toolbar 셋팅
        initToolbar("메뉴리스트")

        showProgress(true)

        mContext = this
        str_menuType = "all"
        str_orderType = "COUNT"
        str_order = "ASC"
        getMenuList(str_menuType, str_orderType, str_order)

        init();


    }


    private fun init() {

        binding.tvAbcOrd.setOnClickListener {
            str_orderType = "ABC"
            getMenuList(str_menuType, str_orderType, str_order)
        }
        binding.tvRecentOrd.setOnClickListener {
            str_orderType = "RECENT"
            getMenuList(str_menuType, str_orderType, str_order)
        }
        binding.tvCountOrd.setOnClickListener {
            str_orderType = "COUNT"
            getMenuList(str_menuType, str_orderType, str_order)
        }
        binding.tvOrder.setOnClickListener {
            if (str_order == "ASC") {
                str_order = "DESC"
                binding.tvOrder.text = "내림차순"
                val drawable = resources.getDrawable(R.drawable.down_icon)
                binding.ivOrder.setImageDrawable(drawable)
            } else {
                str_order = "ASC"
                binding.tvOrder.text = "오름차순"
                val drawable = resources.getDrawable(R.drawable.up_icon)
                binding.ivOrder.setImageDrawable(drawable)
            }
            getMenuList(str_menuType, str_orderType, str_order)
        }
    }


    override fun onPause() {
        super.onPause()
        // overridePendingTransition(0, 0)
    }

    // 프로그레스바 함수
    fun showProgress(isShow: Boolean) {
        if (isShow)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE

    }

    // 메뉴 리스트 가져오는 통신 함수
    private fun getMenuList(menuType: String?, orderType: String?, order: String?) {
        when (str_orderType) {
            "COUNT" -> {
                setToggle(binding.tvCountOrd, binding.tvRecentOrd, binding.tvAbcOrd)
            }
            "ABC" -> {
                setToggle(binding.tvAbcOrd, binding.tvCountOrd, binding.tvRecentOrd)
            }
            "RECENT" -> {
                setToggle(binding.tvRecentOrd, binding.tvAbcOrd, binding.tvCountOrd)
            }
            else -> throw IllegalStateException("Unexpected value: $str_orderType")
        }
        val values = JSONObject()
        try {
            values.put("menuType", menuType)
            values.put("orderType", orderType)
            values.put("order", order)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var url = server_url + path
        val networkTask: NetworkTask = NetworkTask(url, values)
        networkTask.execute()
        showProgress(true)
    }

    private fun setToggle(select: TextView, unselected1: TextView, unselected2: TextView) {
        // 첫번째 인자가 선택된(click) 객체
        select.background = ContextCompat.getDrawable(this, R.drawable.ic_roundbtn)
        select.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorWhite))
        unselected1.background = null
        unselected1.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorGray))
        unselected2.background = null
        unselected2.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorGray))

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
            val adapter = MenuAdapter(list!!, mContext)


            binding.rvMenu.adapter = adapter
            showProgress(false)
        }
    }
}
