package com.lunchteam.lunch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lunchteam.lunch.databinding.ActivityBaseBinding
import com.lunchteam.lunch.databinding.ActivityLunchAllMenuListBinding
import com.lunchteam.lunch.mamber.SignInActivity
import com.lunchteam.lunch.util.MyApplication

open class BaseActivity : AppCompatActivity() {

    internal var server_url=""
    internal var imm: InputMethodManager? = null

    private lateinit var binding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        server_url = resources.getString(R.string.server_url)
        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE)as InputMethodManager?


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.toolbar_logout -> {
                var i = Intent(this, SignInActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ResourceAsColor")
    public fun initToolbar(title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar!!
        actionbar.title = title
        actionbar.setDisplayShowTitleEnabled(true)
        actionbar.setDisplayShowCustomEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

    }

    // 키보드 이벤트
    fun hideKeyboard(v: View){
        if(v !=null)
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

}