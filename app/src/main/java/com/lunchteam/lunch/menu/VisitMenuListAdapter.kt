package com.lunchteam.lunch.menu

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VisitMenuListAdapter(private val itemList: ArrayList<JSONObject>, context: Context?, var deleteUrl: String) : RecyclerView.Adapter<VisitMenuListAdapter.MenuViewHolder>() {
    private val mContext = context

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.visit_restaurant_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bt_delete: ImageView = view.findViewById(R.id.bt_delete)
        private val txt_visit_date: TextView = view.findViewById(R.id.tv_visit_date)
        private val txt_restaurant_name: TextView = view.findViewById(R.id.tv_restaurant_name)
        private val txt_gubun: TextView = view.findViewById(R.id.tv_gubun)

        fun bind(menu: JSONObject) {

            val id = menu.getInt("id")
            val location = menu.getString("location")
            val name = menu.getString("name")
            val menuName = menu.getString("menuName")
            val insertDateTime = menu.getString("insertDateTime")

            txt_restaurant_name.text = name
            txt_visit_date.text = insertDateTime.substring(0, insertDateTime.indexOf("T"))
            txt_gubun.text = menuName


            bt_delete.setOnClickListener {
                deleteMenu(id)
            }

        }
    }


    fun deleteMenu(id: Int) {
        val values = JSONObject()
        try {
            values.put("id", id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val networkTask: NetworkTask = NetworkTask(deleteUrl, values)
        networkTask.execute()
    }


    inner class NetworkTask : AsyncTask<Void, Void, String> {
        private var url: String
        private var values: ContentValues? = null
        private var valuesJson: JSONObject? = null

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
            if (s == "") {
                Toast.makeText(mContext, "방문기록 삭제 완료", Toast.LENGTH_SHORT).show()
                val intent: Intent = (mContext as Activity).intent
                (mContext as Activity).finish() //현재 액티비티 종료 실시
                (mContext as Activity).overridePendingTransition(0, 0) //효과 없애기
                (mContext as Activity).startActivity(intent) //현재 액티비티 재실행 실시
                (mContext as Activity).overridePendingTransition(0, 0) //효과 없애기
            } else {
                Toast.makeText(mContext, "방문기록삭제 실패(로그확인필요)", Toast.LENGTH_SHORT).show()
                Log.e("deleteVisit", "removeFail - " + s.toString())
            }
        }

    }

}