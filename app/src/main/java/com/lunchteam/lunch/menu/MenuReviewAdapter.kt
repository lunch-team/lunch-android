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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.util.MyApplication
import me.relex.circleindicator.CircleIndicator
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MenuReviewAdapter(private val reviewList: ArrayList<JSONObject>, context: Context?, var url: String, var removeUrl: String) : RecyclerView.Adapter<MenuReviewAdapter.MenuViewHolder>() {
    private val mContext = context
    private var targetId = 0

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txt_memberName: TextView = view.findViewById(R.id.tv_memberName)
        private val txt_star: TextView = view.findViewById(R.id.tv_star)
        private val txt_date: TextView = view.findViewById(R.id.tv_date)
        private val txt_contents: TextView = view.findViewById(R.id.tv_contents)
        private val ll_img: LinearLayout = view.findViewById(R.id.ll_img)
        private val vpImg: ViewPager = view.findViewById(R.id.vp_img)
        private val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        private val bt_delete: ImageView = view.findViewById(R.id.bt_delete)
        private lateinit var str_files: String


        fun bind(menu: JSONObject) {
            targetId = menu.getInt("menuId")          // 메뉴 아이디
            var reviewId = menu.getInt("id")                // 리뷰 아이디(번호)
            val insert_memberId = menu.getString("insertMemberId") // 작성자 ID(번호)
            val name = menu.getString("memberName")         // 작성자 이름
            val getStar = menu.getInt("star")                  // 별점
            val dateTime = menu.getString("insertDateTime") // 등록시간
            val contents = menu.getString("contents")       // 내용
            str_files = menu.get("files").toString()               // 첨부파일

            txt_memberName.text = name
            var star = 0
            // 초기에 리뷰 10점으로 들어간 부분 수정 적용
            star = if (5 < getStar)
                getStar / 2
            else
                getStar
            when (star) {
                0 -> txt_star.text = "☆☆☆☆☆"
                1 -> txt_star.text = "★☆☆☆☆"
                2 -> txt_star.text = "★★☆☆☆"
                3 -> txt_star.text = "★★★☆☆"
                4 -> txt_star.text = "★★★★☆"
                5 -> txt_star.text = "★★★★★"
            }

            txt_date.text = dateTime.substring(0, dateTime.indexOf("T"))
            txt_contents.text = contents

            val loginId = MyApplication.prefs.getString("memberId", "")
            if (loginId == insert_memberId) {
                bt_delete.visibility = View.VISIBLE
                bt_delete.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(mContext!!)
                    alertDialog.setTitle("알림")
                    alertDialog.setMessage("리뷰를 삭제하시겠습니까?")
                    alertDialog.setPositiveButton("확인") { _, _ -> removeReview(reviewId) }
                    alertDialog.setNegativeButton("취소", null)
                    alertDialog.create()
                    alertDialog.show()


                }
            } else {
                bt_delete.visibility = View.GONE
            }



            if (!str_files.equals("null")) {
                ll_img.visibility = View.VISIBLE

                val files = JSONArray(str_files)

                //var imgAdapter =
                vpImg.adapter = MenuReviewImgAdapter(files, mContext!!, url)
                if (files.length() > 1)
                    indicator.visibility = View.VISIBLE
                else
                    indicator.visibility = View.GONE
                indicator.setViewPager(vpImg);
            } else
                ll_img.visibility = View.GONE

        }

    }

    fun removeReview(id: Int) {
        val values = JSONObject()
        try {
            values.put("id", id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val networkTask: NetworkTask = NetworkTask(removeUrl, values)
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
                Toast.makeText(mContext, "리뷰삭제 완료", Toast.LENGTH_SHORT).show()
                val intent: Intent = (mContext as Activity).intent
                (mContext as Activity).finish() //현재 액티비티 종료 실시
                intent.putExtra("id", targetId)          // 메뉴 아이디)

                (mContext as Activity).overridePendingTransition(0, 0) //효과 없애기

                (mContext as Activity).startActivity(intent) //현재 액티비티 재실행 실시

                (mContext as Activity).overridePendingTransition(0, 0) //효과 없애기

            } else {
                Toast.makeText(mContext, "리뷰삭제 실패(로그확인필요)", Toast.LENGTH_SHORT).show()
                Log.e("review", "removeFail - " + s.toString())
            }
        }

    }
}

