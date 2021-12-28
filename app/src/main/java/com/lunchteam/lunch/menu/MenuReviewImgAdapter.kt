package com.lunchteam.lunch.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.lunchteam.lunch.R
import org.json.JSONArray
import org.json.JSONObject

class MenuReviewImgAdapter(private val imgList: JSONArray, private var context: Context, private var url: String) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imgList.length()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.viewpager_photo, null)
        val image = v.findViewById<View>(R.id.iv_review_img) as ImageView
        //todo 이미지 경로 넣어주기

        var file_nm: String = (imgList.get(position) as JSONObject).getString("storedFileName")
        var imageUrl = url + file_nm
        Glide.with(context).load(imageUrl).into(image)

        val vp = container as ViewPager
        vp.addView(v, 0)

        return v
        //return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
        //super.destroyItem(container, position, `object`)
    }
}