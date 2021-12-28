package com.lunchteam.lunch.menu

import android.content.Context
import android.os.Bundle
import com.lunchteam.lunch.BaseActivity
import com.lunchteam.lunch.databinding.ActivityLunchVisitMenuListBinding

class LunchVisitMenuList : BaseActivity() {
    private var mContext: Context? = null
    private var path = ""

    private lateinit var binding: ActivityLunchVisitMenuListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunchVisitMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
    }
}