package com.lunchteam.lunch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.lunchteam.lunch.databinding.ActivityMainBinding
import com.lunchteam.lunch.mamber.SignInActivity
import com.lunchteam.lunch.menu.*


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btList.setOnClickListener{
            var i = Intent(this, LunchAllMenuList::class.java)
            startActivity(i)
        }


        //툴바 셋팅
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar!!
        actionbar.title = "점심 뭐 먹지?"
        actionbar.setDisplayShowTitleEnabled(true)
        actionbar.setDisplayShowCustomEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(false)



        binding.btRandom.setOnClickListener{
            var i = Intent(this, LunchRandomMenu::class.java)
            startActivity(i)
        }

        binding.btAddMenu.setOnClickListener{
            var i = Intent(this, LunchAddMenu::class.java)
            startActivity(i)
        }
        binding.btReview.setOnClickListener{
            var i = Intent(this, LunchWriteReview::class.java)
            startActivity(i)
        }
        binding.btVisit.setOnClickListener{
            var i = Intent(this, LunchVisitMenuList::class.java)
            startActivity(i)
        }
        binding.btLogin.setOnClickListener{
            var i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }



        binding.btMe.setOnClickListener{
            var i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }

    }

    // 툴바 셋팅
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}