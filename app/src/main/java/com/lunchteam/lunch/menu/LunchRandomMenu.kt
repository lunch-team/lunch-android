package com.lunchteam.lunch.menu

import android.content.Context
import android.os.Bundle
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.databinding.ActivityLunchRandomMenuBinding

class LunchRandomMenu : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""

    private lateinit var binding: ActivityLunchRandomMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchRandomMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
    }
}