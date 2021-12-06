package com.lunchteam.lunch.menu;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lunchteam.lunch.BaseActivity;
import com.lunchteam.lunch.R;
import com.lunchteam.lunch.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LunchAllMenuList extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private String url = "http://192.168.0.15:9090/menu/getAllMenu";  // AsyncTask를 통해 HttpURLConnection 수행.
    private ArrayList<JSONObject> list;

    private TextView tv_abc;
    private TextView tv_recent;
    private TextView tv_count;
    private TextView tv_order;
    private ImageView iv_order;
    private String str_order;
    private String str_menuType;
    private String str_orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch_all_menu_list);

        mContext = this;
        init();

        ImageView iv_back = findViewById(R.id.iv_icon);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        str_menuType = "all";
        str_orderType = "COUNT";
        str_order = "ASC";

        getMenuList(str_menuType, str_orderType, str_order);


    }

    public void init() {
        tv_abc = findViewById(R.id.tv_abc_ord);
        tv_abc.setOnClickListener(this);
        tv_recent = findViewById(R.id.tv_recent_ord);
        tv_recent.setOnClickListener(this);
        tv_count = findViewById(R.id.tv_count_ord);
        tv_count.setOnClickListener(this);

        tv_order = findViewById(R.id.tv_order);
        tv_order.setOnClickListener(this);
        iv_order = findViewById(R.id.iv_order);
        iv_order.setOnClickListener(this);
    }

    // 메뉴 리스트 가져오는 통신 함수
    private void getMenuList(String menuType, String orderType, String order) {
        switch (str_orderType) {
            case "COUNT": {
                tv_count.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_roundbtn));
                tv_count.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                tv_recent.setBackground(null);
                tv_recent.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
                tv_abc.setBackground(null);
                tv_abc.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
            }
            break;
            case "ABC": {
                tv_abc.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_roundbtn));
                tv_abc.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                tv_recent.setBackground(null);
                tv_recent.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
                tv_count.setBackground(null);
                tv_count.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
            }
            break;

            case "RECENT": {
                tv_recent.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_roundbtn));
                tv_recent.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                tv_abc.setBackground(null);
                tv_abc.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
                tv_count.setBackground(null);
                tv_count.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
            }
            break;


            default:
                throw new IllegalStateException("Unexpected value: " + str_orderType);
        }

        JSONObject values = new JSONObject();
        try {
            values.put("menuType", menuType);
            values.put("orderType", orderType);
            values.put("order", order);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_abc_ord: {
                str_orderType = "ABC";
                getMenuList(str_menuType, str_orderType, str_order);
            }
            break;
            case R.id.tv_recent_ord: {
                str_orderType = "RECENT";
                getMenuList(str_menuType, str_orderType, str_order);
            }
            break;
            case R.id.tv_count_ord: {
                str_orderType = "COUNT";
                getMenuList(str_menuType, str_orderType, str_order);
            }
            break;
            case R.id.tv_order:
            case R.id.iv_order: {
                if (str_order.equals("ASC")) {
                    str_order = "DESC";
                    tv_order.setText("내림차순");
                    Drawable drawable = getResources().getDrawable(R.drawable.down_icon);
                    iv_order.setImageDrawable(drawable);

                } else {
                    str_order = "ASC";
                    tv_order.setText("오름차순");
                    Drawable drawable = getResources().getDrawable(R.drawable.up_icon);
                    iv_order.setImageDrawable(drawable);

                }
                getMenuList(str_menuType, str_orderType, str_order);
            }

            break;


        }
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
            JSONArray menulist;
            int count;
            list = new ArrayList<>();


            try {
                JSONObject data = new JSONObject(s);
                count = data.getInt("count");
                menulist = new JSONArray(data.getString("data"));
                for (int i = 0; i < count; i++) {
                    JSONObject menu = menulist.getJSONObject(i);
                    list.add(menu);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            RecyclerView recyclerView = findViewById(R.id.rv_menu);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            MenuAdapter adapter = new MenuAdapter(list);
            recyclerView.setAdapter(adapter);

        }
    }

}