package com.lunchteam.lunch.menu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lunchteam.lunch.R
import org.json.JSONObject
import java.util.*

class MenuAdapter(private val itemList: ArrayList<JSONObject>, context: Context?) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private val mContext = context

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txt_manu: TextView = view.findViewById(R.id.tv_menu)
        private val txt_type: TextView = view.findViewById(R.id.tv_type)
        private val txt_visit: TextView = view.findViewById(R.id.tv_visit)
        private val txt_star: TextView = view.findViewById(R.id.tv_reviewpt)

        fun bind(menu: JSONObject) {

            val id = menu.getInt("id")
            val location = menu.getString("location")
            val name = menu.getString("name")
            val menuType = menu.getString("menuType")
            val menuName = menu.getString("menuName")
            val visitCount = menu.getInt("visitCount")
            val recentVisit = menu.getString("recentVisit")
            val insertDateTime = menu.getString("insertDateTime")
            val star = menu.getDouble("star")


            txt_manu.text = name
            txt_visit.text = Integer.toString(visitCount)
            txt_type.text = menuName
            if (star > 0) {
                txt_star.text = star.toString()
            }else {
                txt_star.text = "-"
            }

            itemView.setOnClickListener {
                Intent(mContext, LunchMenuDetail::class.java).apply {
                    putExtra("id", id)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { mContext?.startActivity(this) }
            }

        }
    }


}