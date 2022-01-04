package com.lunchteam.lunch.menu

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.databinding.ActivityLunchWriteReviewBinding
import kr.co.lia.photopicker.utils.YPhotoPickerIntent


class LunchWriteReview : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var menuId = 0
    private var menuName = ""
    private var selectStar = 5.0

    private val REQUEST_CODE = 273

    private lateinit var binding: ActivityLunchWriteReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

        path = resources.getString(R.string.upload_review_path)

        // toolbar 셋팅
        initToolbar("리뷰작성")
        // 클릭한 식당 id 값 가져오기
        menuId = intent.getIntExtra("id", 0)
        menuName = intent.getStringExtra("name").toString()
        binding.tvName.text = menuName

        // 별점 주기
        binding.llStar.setOnTouchListener { view, motionEvent ->
            val action = motionEvent.action
            val curX = motionEvent.x
            val curY = motionEvent.y
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("dytest", "Touch down : $curX, $curY")
                }
                MotionEvent.ACTION_MOVE -> {

                    if (curX < 0)
                        setStar(0.0)
                    else if (0 < curX && curX < 50)
                        setStar(0.5)
                    else if (50 < curX && curX < 100)
                        setStar(1.0)
                    else if (100 < curX && curX < 150)
                        setStar(1.5)
                    else if (150 < curX && curX < 200)
                        setStar(2.0)
                    else if (200 < curX && curX < 250)
                        setStar(2.5)
                    else if (250 < curX && curX < 300)
                        setStar(3.0)
                    else if (300 < curX && curX < 350)
                        setStar(3.5)
                    else if (350 < curX && curX < 400)
                        setStar(4.0)
                    else if (400 < curX && curX < 450)
                        setStar(4.5)
                    else if (450 < curX && curX < 500)
                        setStar(4.5)
                    else if (500 < curX)
                        setStar(5.0)
                }
                MotionEvent.ACTION_UP -> {
                    Log.e("dytest", "Touch up : $curX, $curY")
                }
            }
            true
        }



        binding.btSelectPhoto.setOnClickListener(View.OnClickListener {
            val intent = YPhotoPickerIntent(this)
            intent.setMaxSelectCount(10) // 2020-03-24 최다영 추가, 해상도 값  나중에 변수로 넣기
            intent.setShowCamera(false) //주석 기존엔 true
            intent.setShowGif(false)
            intent.setSelectCheckBox(false)
            intent.setMaxGrideItemCount(4)
            intent.setPhotoQuality(1000) // 2020-03-24 최다영 추가, 해상도 값  나중에 변수로 넣기
            startActivityForResult(intent, REQUEST_CODE)
        })
    }

    private fun setStar(star: Double) {
        selectStar = star
        when (star) {
            0.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_no_star)
                binding.ivStar2.setImageResource(R.drawable.img_no_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            0.5 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star_harf)
                binding.ivStar2.setImageResource(R.drawable.img_no_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            1.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_no_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            1.5 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star_harf)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            2.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            2.5 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star_harf)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            3.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            3.5 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_star_harf)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }

            4.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            4.5 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_star)
                binding.ivStar5.setImageResource(R.drawable.img_star_harf)
            }
            5.0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_star)
                binding.ivStar5.setImageResource(R.drawable.img_star)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoMultiSelectResult(requestCode, resultCode, data)) return;
    }

    protected fun photoMultiSelectResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return true;
        }
        var selectResult = ""
        when (requestCode) {
            REQUEST_CODE -> if (resultCode == RESULT_OK) {
                val bundle = data?.extras
                val sPath = bundle!!.getStringArrayList("path")
                if (sPath!!.size > 0) {
                    selectResult = sPath[0]
                    var i = 1
                    while (i < sPath.size) {
                        selectResult = selectResult + "|" + sPath[i]
                        i++
                    }
                }
                Log.e("???", selectResult)
                selectPhotoList(sPath)
                return true
            }
        }
        Log.e("???", "selectResult$selectResult")
        return false
    }


    private fun selectPhotoList(path: ArrayList<*>) {

        val items: ArrayList<String?> = path as ArrayList<String?>
        val adapter = CustomAdapter(items, mContext)
        //recyclerView 가로스크롤 적용
        binding.lvPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.lvPhoto.adapter = adapter

    }

    class CustomAdapter(val itemList: ArrayList<String?>, context: Context?) : RecyclerView.Adapter<CustomAdapter.PhotoHolder>() {
        val mContext: Context? = context
        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.write_review_photo_item, parent, false)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(itemList[position])
        }

        inner class PhotoHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val imageView: ImageView = view.findViewById(R.id.iv_photo)
            private val delete: ImageView = view.findViewById(R.id.bt_delete)

            fun bind(path: String?) {

                val options = BitmapFactory.Options()
                val originalBm = BitmapFactory.decodeFile(path, options)

                // ImageView 인스턴스
                imageView.setImageBitmap(originalBm)


                delete.setOnClickListener {
                    itemList.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }

    }


}