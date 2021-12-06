package com.lunchteam.lunch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lunchteam.lunch.mamber.SignInActivity;
import com.lunchteam.lunch.menu.LunchAddMenu;
import com.lunchteam.lunch.menu.LunchAllMenuList;
import com.lunchteam.lunch.menu.LunchRandomMenu;
import com.lunchteam.lunch.menu.LunchVisitMenuList;
import com.lunchteam.lunch.menu.LunchWriteReview;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;

    Button bt_addmenu;
    Button bt_menuList;
    Button bt_random;
    Button bt_visit;
    Button bt_review;
    Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;


        initBtn();
    }

    public void initBtn() {
        bt_addmenu = findViewById(R.id.bt_addMenu);
        bt_addmenu.setOnClickListener(this);
        bt_menuList = findViewById(R.id.bt_list);
        bt_menuList.setOnClickListener(this);
        bt_random = findViewById(R.id.bt_random);
        bt_random.setOnClickListener(this);
        bt_visit = findViewById(R.id.bt_visit);
        bt_visit.setOnClickListener(this);
        bt_review = findViewById(R.id.bt_review);
        bt_review.setOnClickListener(this);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bt_random :
                intent = new Intent(mContext, LunchRandomMenu.class);
                startActivity(intent);
                break;
            case R.id.bt_list :
                intent = new Intent(mContext, LunchAllMenuList.class);
                startActivity(intent);
                break;
            case R.id.bt_addMenu :
                intent = new Intent(mContext, LunchAddMenu.class);
                startActivity(intent);
                break;
            case R.id.bt_review :
                intent = new Intent(mContext, LunchWriteReview.class);
                startActivity(intent);
                break;
            case R.id.bt_visit :
                intent = new Intent(mContext, LunchVisitMenuList.class);
                startActivity(intent);
                break;
            case R.id.bt_login :
                intent = new Intent(mContext, SignInActivity.class);
                startActivity(intent);
                break;


        }

    }



}