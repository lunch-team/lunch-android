package com.lunchteam.lunch.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lunchteam.lunch.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MenuAdapter internal constructor(list: ArrayList<JSONObject>?) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var mData: ArrayList<JSONObject>? = null

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_menu: TextView
        var tv_visit: TextView
        var tv_type: TextView

        init {

            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_menu = itemView.findViewById(R.id.tv_menu)
            tv_visit = itemView.findViewById(R.id.tv_visit)
            //tv_reviewpt = itemView.findViewById(R.id.tv_reviewpt) ;
            tv_type = itemView.findViewById(R.id.tv_type)
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.menu_item, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = mData!![position]
        try {
            val id = menu.getInt("id")
            val location = menu.getString("location")
            val name = menu.getString("name")
            val menuType = menu.getString("menuType")
            val menuName = menu.getString("menuName")
            val visitCount = menu.getInt("visitCount")
            val recentVisit = menu.getString("recentVisit")
            val insertDateTime = menu.getString("insertDateTime")
            holder.tv_menu.text = name
            holder.tv_visit.text = Integer.toString(visitCount)
            holder.tv_type.text = menuName
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    override fun getItemCount(): Int {
        return mData!!.size
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    init {
        mData = list
    }
}