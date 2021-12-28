package com.lunchteam.lunch.mamber

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivitySignInBinding
import com.lunchteam.lunch.util.MyApplication
import org.json.JSONException
import org.json.JSONObject

class SignInActivity : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = resources.getString(R.string.sign_in_path)

        mContext = this

        init();
        initToolbar("")
    }

    private fun init() {
        binding.btLogin.setOnClickListener { login() }
        binding.tvSignup.setOnClickListener {
            val i = Intent(mContext, SignUpActivity::class.java)
            startActivity(i)
        }
    }


    // 로그인 통신 함수
    private fun login() {
        if (binding.etId.text.toString() == "" || binding.etPassword.text.toString() == "") {
            Toast.makeText(mContext, "아이디 및 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return;
        }
        val values = JSONObject()
        try {
            values.put("loginId", binding.etId.text)
            values.put("password", binding.etPassword.text)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var url = server_url + path
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

        override fun doInBackground(vararg p0: Void?): String? {
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
            if (s == "401") {
                Toast.makeText(mContext, "아이디 및 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val loginToken: JSONObject
                val memberInfo: JSONObject
                val count: Int
                try {
                    val data = JSONObject(s)
                    loginToken = data.getJSONObject("token")

                    MyApplication.prefs.setString("accessToken", loginToken.get("accessToken").toString())
                    MyApplication.prefs.setString("refreshToken", loginToken.get("refreshToken").toString())
                    MyApplication.prefs.setString("accessTokenExpiresIn", loginToken.get("accessTokenExpiresIn").toString())

                    // 자동로그인
                    MyApplication.prefs.setString("autoLogin", "0")
                    MyApplication.prefs.setString("isLogin", "1")


                    memberInfo = data.getJSONObject("memberInfo")
                    Toast.makeText(mContext, memberInfo.get("name").toString() + "님 안녕하세요", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Log.d("dytest", "accessToken : " + MyApplication.prefs.getString("accessToken", "no accessToken"))
                Log.d("dytest", "refreshToken : " + MyApplication.prefs.getString("refreshToken", "no refreshToken"))
                Log.d("dytest", "accessTokenExpiresIn : " + MyApplication.prefs.getString("accessTokenExpiresIn", "no accessTokenExpiresIn"))

            }

        }
    }
}