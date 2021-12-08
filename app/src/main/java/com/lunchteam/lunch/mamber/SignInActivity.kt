package com.lunchteam.lunch.mamber

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : BaseActivity(), View.OnClickListener {
    private var mContext: Context? = null
    private val url = "http://192.168.0.15:9090/auth/login" // AsyncTask를 통해 HttpURLConnection 수행.
    var tv_id: TextView? = null
    var tv_password: TextView? = null
    var bt_login: Button? = null
    var tv_findid: TextView? = null
    var tv_findpassword: TextView? = null
    var tv_signup: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        mContext = this
        init()
    }

    fun init() {
        tv_id = findViewById(R.id.tv_id)
        tv_password = findViewById(R.id.tv_password)
        bt_login = findViewById(R.id.bt_login)
        tv_findid = findViewById(R.id.tv_findid)
        tv_findpassword = findViewById(R.id.tv_findpassword)
        tv_signup = findViewById(R.id.tv_signup)
    }

    override fun onClick(v: View) {
       /* when (v.id) {
            R.id.bt_login -> login()
            R.id.tv_findid -> {
            }
            R.id.tv_findpassword -> {
            }
            R.id.tv_signup -> {
                val i = Intent(mContext, SignUpActivity::class.java)
                startActivity(i)
            }
        }*/
    }
/*
    // 로그인 통신 함수
    private fun login() {
        val values = JSONObject()
        try {
            values.put("loginId", tv_id!!.text)
            values.put("password", tv_password!!.text)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val networkTask: NetworkTask = NetworkTask(url, values)
        networkTask.execute()
    }

    inner class NetworkTask : AsyncTask<Void?, Void?, String?> {
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

        protected override fun doInBackground(vararg params: Void): String? {
            val result: String // 요청 결과를 저장할 변수.
            val requestHttpURLConnection = RequestHttpURLConnection()
            result = if (values != null) {
                requestHttpURLConnection.request(url, values) // 해당 URL로 부터 결과물을 얻어온다.
            } else {
                requestHttpURLConnection.request(url, valuesJson)
            }
            values = null
            return result
        }

        public override fun onPostExecute(s: String?) {
            super.onPostExecute(s) //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            if (s == "401") {
                Toast.makeText(mContext, "아이디 및 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}