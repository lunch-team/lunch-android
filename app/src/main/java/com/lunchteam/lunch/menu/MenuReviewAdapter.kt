package com.lunchteam.lunch.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.lunchteam.lunch.R
import me.relex.circleindicator.CircleIndicator
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter


class MenuReviewAdapter(private val reviewList: ArrayList<JSONObject>, context: Context?, var url: String) : RecyclerView.Adapter<MenuReviewAdapter.MenuViewHolder>() {
    private val mContext = context

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
        private lateinit var str_files: String
        fun bind(menu: JSONObject) {

            val name = menu.getString("memberName")
            val star = menu.getInt("star")
            val dateTime = menu.getString("insertDateTime")
            val contents = menu.getString("contents")
            str_files = menu.get("files").toString()

            txt_memberName.text = name
            when (star / 2) {
                0 -> txt_star.text = "☆☆☆☆☆"
                1 -> txt_star.text = "★☆☆☆☆"
                2 -> txt_star.text = "★★☆☆☆"
                3 -> txt_star.text = "★★★☆☆"
                4 -> txt_star.text = "★★★★☆"
                5 -> txt_star.text = "★★★★★"
            }

            txt_date.text = dateTime.substring(0, dateTime.indexOf("T"))
            txt_contents.text = contents



            if (!str_files.equals("null")) {
                ll_img.visibility = View.VISIBLE

                val files = JSONArray(str_files)

                //var imgAdapter =
                vpImg.adapter = MenuReviewImgAdapter(files, mContext!!, url)
                if (files.length() > 1)
                    indicator.setViewPager(vpImg);
            } else
                ll_img.visibility = View.GONE

        }
    }


}