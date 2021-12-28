package com.lunchteam.lunch.mamber

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.R
import com.lunchteam.lunch.RequestHttpURLConnection
import com.lunchteam.lunch.databinding.ActivitySignUpBinding
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        path = resources.getString(R.string.sign_up_path)

        mContext = this


        init();


        binding.etId.requestFocus();
        imm?.showSoftInput(binding.etId, 0)
    }


    private fun init() {
        binding.btSignup.setOnClickListener { signup() }
    }

    // 회원가입 체크
    private fun signup() {
        // 입력값 체크
        when {
            binding.etId.text.toString() == "" -> {
                Toast.makeText(mContext, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.etId.requestFocus();
                imm?.showSoftInput(binding.etId, 0)
                return
            }
            binding.etName.text.toString() == "" -> {
                Toast.makeText(mContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.etName.requestFocus();
                imm?.showSoftInput(binding.etName, 0)
                return
            }
            binding.etEmail.text.toString() == "" -> {
                Toast.makeText(mContext, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.etEmail.requestFocus();
                imm?.showSoftInput(binding.etEmail, 0)
                return
            }
            binding.etPassword.text.toString() == "" -> {
                Toast.makeText(mContext, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus();
                imm?.showSoftInput(binding.etPassword, 0)
                return
            }
            binding.etCheckPassword.text.toString() == "" -> {
                Toast.makeText(mContext, "비밀번호 확인란을 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.etCheckPassword.requestFocus();
                imm?.showSoftInput(binding.etCheckPassword, 0)
                return
            }
            binding.etPassword.text.toString() != binding.etCheckPassword.text.toString() -> {
                Toast.makeText(mContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                binding.etCheckPassword.requestFocus();
                imm?.showSoftInput(binding.etCheckPassword, 0)
                return
            }
        }

        val values = JSONObject()
        try {
            values.put("email", binding.etEmail.text)
            values.put("loginId", binding.etId.text)
            values.put("name", binding.etName.text)
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
            if (s == "409") {
                Toast.makeText(mContext, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
            } else if (s == "") {
                Toast.makeText(mContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish();
            }

        }
    }
}