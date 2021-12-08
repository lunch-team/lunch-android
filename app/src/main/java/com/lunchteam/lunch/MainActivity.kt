package com.lunchteam.lunch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

    }

}