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
            targetId = menu.getInt("menuId")          // ?????? ?????????
            var reviewId = menu.getInt("id")                // ?????? ?????????(??????)
            val insert_memberId = menu.getString("insertMemberId") // ????????? ID(??????)
            val name = menu.getString("memberName")         // ????????? ??????
            val getStar = menu.getInt("star")                  // ??????
            val dateTime = menu.getString("insertDateTime") // ????????????
            val contents = menu.getString("contents")       // ??????
            str_files = menu.get("files").toString()               // ????????????

            txt_memberName.text = name
            var star = 0
            // ????????? ?????? 10????????? ????????? ?????? ?????? ??????
            star = if (5 < getStar)
                getStar / 2
            else
                getStar
            when (star) {
                0 -> txt_star.text = "???????????????"
                1 -> txt_star.text = "???????????????"
                2 -> txt_star.text = "???????????????"
                3 -> txt_star.text = "???????????????"
                4 -> txt_star.text = "???????????????"
                5 -> txt_star.text = "???????????????"
            }

            txt_date.text = dateTime.substring(0, dateTime.indexOf("T"))
            txt_contents.text = contents

            val loginId = MyApplication.prefs.getString("memberId", "")
            if (loginId == insert_memberId) {
                bt_delete.visibility = View.VISIBLE
                bt_delete.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(mContext!!)
                    alertDialog.setTitle("??????")
                    alertDialog.setMessage("????????? ?????????????????????????")
                    alertDialog.setPositiveButton("??????") { _, _ -> removeReview(reviewId) }
                    alertDialog.setNegativeButton("??????", null)
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
            val result: String? // ?????? ????????? ????????? ??????.
            val requestHttpURLConnection = RequestHttpURLConnection()

            result = if (values != null)
                requestHttpURLConnection.request(url, values!!) // ?????? URL??? ?????? ???????????? ????????????.
            else
                requestHttpURLConnection.request(url, valuesJson)
            return result
        }

        public override fun onPostExecute(s: String?) {
            super.onPostExecute(s) //doInBackground()??? ?????? ????????? ?????? onPostExecute()??? ??????????????? ??????????????? s??? ????????????.
            if (s == "") {
                Toast.makeText(mContext, "???????????? ??????", Toast.LENGTH_SHORT).show()
                val intent: Intent = (mContext as Activity).intent
                (mContext as Activity).finish() //?????? ???????????? ?????? ??????
                intent.putExtra("id", targetId)          // ?????? ?????????)

                (mContext as Activity).overridePendingTransition(0, 0) //?????? ?????????

                (mContext as Activity).startActivity(intent) //?????? ???????????? ????????? ??????

                (mContext as Activity).overridePendingTransition(0, 0) //?????? ?????????

            } else {
                Toast.makeText(mContext, "???????????? ??????(??????????????????)", Toast.LENGTH_SHORT).show()
                Log.e("review", "removeFail - " + s.toString())
            }
        }

    }
}

