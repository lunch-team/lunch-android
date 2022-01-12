package com.lunchteam.lunch.menu

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivityLunchWriteReviewBinding
import com.lunchteam.lunch.util.MyApplication
import kr.co.lia.photopicker.utils.YPhotoPickerIntent
import okhttp3.*
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class LunchWriteReview : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""
    private var uploadPath = ""
    private var menuId = 0
    private var menuName = ""
    private var selectStar = 5
    private val REQUEST_CODE = 273
    private var isSelect = false

    private lateinit var binding: ActivityLunchWriteReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

        // 통신 url
        path = resources.getString(R.string.upload_review_path)
        uploadPath = resources.getString(R.string.upload_photo_path)

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
                        setStar(0)
                    else if (0 < curX && curX < 150)
                        setStar(1)
                    else if (150 < curX && curX < 250)
                        setStar(2)
                    else if (250 < curX && curX < 350)
                        setStar(3)
                    else if (350 < curX && curX < 450)
                        setStar(4)
                    else if (450 < curX && curX < 550)
                        setStar(5)
                }
                MotionEvent.ACTION_UP -> {
                    Log.e("dytest", "Touch up : $curX, $curY")
                }
            }
            true
        }


        binding.btSelectPhoto.setOnClickListener(View.OnClickListener {

            val intent = YPhotoPickerIntent(this)
            intent.setMaxSelectCount(0) // 2020-03-24 최다영 추가, 해상도 값  나중에 변수로 넣기
            intent.setShowCamera(false) //주석 기존엔 true
            intent.setShowGif(false)
            intent.setSelectCheckBox(false)
            intent.setMaxGrideItemCount(4)
            intent.setPreview(false)
            intent.setPhotoQuality(1000) // 2020-03-24 최다영 추가, 해상도 값  나중에 변수로 넣기
            startActivityForResult(intent, REQUEST_CODE)
        })

        binding.btSave.setOnClickListener {
            // 선택한 사진 업로드 -> (성공 시) 리뷰내용 업로드
            sendtoReciew()

        }
    }

    private fun setStar(star: Int) {
        selectStar = star
        when (star) {
            0 -> {
                binding.ivStar1.setImageResource(R.drawable.img_no_star)
                binding.ivStar2.setImageResource(R.drawable.img_no_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            1 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_no_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            2 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_no_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            3 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_no_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            4 -> {
                binding.ivStar1.setImageResource(R.drawable.img_star)
                binding.ivStar2.setImageResource(R.drawable.img_star)
                binding.ivStar3.setImageResource(R.drawable.img_star)
                binding.ivStar4.setImageResource(R.drawable.img_star)
                binding.ivStar5.setImageResource(R.drawable.img_no_star)
            }
            5 -> {
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

    // 사진 선택한 결과 값 처리
    protected fun photoMultiSelectResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return true;
        }
        isSelect = true
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

    private lateinit var items: ArrayList<String?>
    private lateinit var adapter: CustomAdapter
    private var firstCheck = false
    private fun selectPhotoList(path: ArrayList<*>) {
        items = path as ArrayList<String?>
        if (firstCheck) {
            var i = 0
            var tempitem = adapter.itemList
            while (i < items.size) {
                tempitem.add(items[i])
                i++
            }
            items = tempitem
        }
        adapter = CustomAdapter(items, mContext)
        firstCheck = true


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

    // 리뷰내용 통신 로직
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

            showProgress(false)
            if (s == "") {
                Toast.makeText(mContext, "리뷰등록 완료", Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else {
                Toast.makeText(mContext, "리뷰등록 실패(로그확인필요)", Toast.LENGTH_SHORT).show()
                Log.e("review", "uploadFail - " + s.toString())
            }
        }

    }


    // 프로그레스바 함수
    fun showProgress(isShow: Boolean) {
        if (isShow)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }

    private fun sendtoReciew() {

        showProgress(true)
        // 사진을 선택한 내역이 있으면 진행
        if (isSelect) {
            if (0 < adapter.itemList.size)
                uploadPhoto(adapter.itemList)
            else
                uploadReview("") // 사진 선택을 했었지만 이미지를 모두 지운 경우
        } else {
            uploadReview("")
        }
    }

    // 첨부파일 업로드 함수
    private fun uploadPhoto(paths: ArrayList<String?>) {
        val multipartBody: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        // 이미지 경로 추가
        var i = 0
        while (i < paths.size) {

            var file: File = File(paths[i])
            var filePath = paths[i]
            var indexPath = filePath?.split("/")

            var fileName = indexPath?.get(7)
            multipartBody.addFormDataPart("files", fileName, RequestBody.create(MultipartBody.FORM, file))
            i++
        }

        // 메뉴번호 추가(targetId)
        multipartBody.addFormDataPart("targetId", menuId.toString())

        // 계정번호 추가(memberId)
        val mId = MyApplication.prefs.getString("memberId", "");
        multipartBody.addFormDataPart("memberId", mId)

        // 위의 소스에서 추가한 multipartBody 데이터로 RequestBody 생성
        val requestBody: RequestBody = multipartBody.build()

        val request = Request.Builder()
                .url(resources.getString(R.string.server_url) + uploadPath)
                .post(requestBody)
                .build()


        // client 생성 및 Callback
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(mContext, "파일 업로드 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {

                var resultbody: JSONObject = JSONObject(response.body?.string())
                var resultDatas = resultbody.getJSONArray("data")

                // jsonArray 에서 첫번째 데이터 가져온 후 groupId 추출하기!!
                var resultData: JSONObject = JSONObject(resultDatas[0].toString())
                var resultGroupID = resultData.get("groupId")


                // 파일 업로드 성공 시
                uploadReview(resultGroupID.toString())
            }
        })

    }

    // 리뷰 내용 업로드
    private fun uploadReview(groupId: String) {
        val values = JSONObject()
        try {
            values.put("contents", binding.etReview.text.toString()) // 리뷰내용
            val memberId = Integer.parseInt(MyApplication.prefs.getString("memberId", "0"))
            values.put("memberId", memberId) // 사용자ID
            values.put("menuId", menuId) // 메뉴ID
            if (groupId != "")
                values.put("fileId", groupId) // 첨부파일 그룹ID
            values.put("star", selectStar) // 별점

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var url = server_url + path
        val networkTask: NetworkTask = NetworkTask(url, values)
        networkTask.execute()
        showProgress(true)
    }


    //뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        // super.onBackPressed() <= 자동적으로 finish 가 적용되기 때문에 requestCode가 0으로 들어가서 주석처리함.

        val returnIntent = Intent()
        returnIntent.putExtra("id", menuId)          // 메뉴 아이디
        setResult(RESULT_OK, returnIntent)
        finish()

    }
}