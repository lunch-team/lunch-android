package com.lunchteam.lunch.mamber;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunchteam.lunch.BaseActivity;
import com.lunchteam.lunch.R;
import com.lunchteam.lunch.RequestHttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;


public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private String url = "http://192.168.0.15:9090/auth/login";  // AsyncTask를 통해 HttpURLConnection 수행.
    TextView tv_id;
    TextView tv_password;
    Button bt_login;
    TextView tv_findid;
    TextView tv_findpassword;
    TextView tv_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mContext = this;

        ImageView iv_back = findViewById(R.id.iv_icon);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();
    }

    public void init() {
        tv_id = findViewById(R.id.tv_id);
        tv_password = findViewById(R.id.tv_password);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        tv_findid = findViewById(R.id.tv_findid);
        tv_findid.setOnClickListener(this);
        tv_findpassword = findViewById(R.id.tv_findpassword);
        tv_findpassword.setOnClickListener(this);
        tv_signup = findViewById(R.id.tv_signup);
        tv_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                login();
                break;
            case R.id.tv_findid:
                break;
            case R.id.tv_findpassword:
                break;
            case R.id.tv_signup:
                Intent i = new Intent(mContext, SignUpActivity.class);
                startActivity(i);
                break;
        }
    }

    // 로그인 통신 함수
    private void login() {
        JSONObject values = new JSONObject();
        try {
            values.put("loginId", tv_id.getText());
            values.put("password", tv_password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        private JSONObject valuesJson;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        public NetworkTask(String url, JSONObject values) {
            this.url = url;
            this.valuesJson = values;

        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            if (values != null) {
                result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            } else {
                result = requestHttpURLConnection.request(url, valuesJson);
            }

            values = null;

            return result;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s); //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            if(s.equals("401")){
                Toast.makeText(mContext, "아이디 및 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        }


    }
}