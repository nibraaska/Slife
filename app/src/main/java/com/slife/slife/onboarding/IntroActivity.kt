package com.slife.slife.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.slife.slife.R
import java.util.*

class   IntroActivity: AppCompatActivity() {

    private var screenPager: ViewPager? = null
    private lateinit var tabIndicator: TabLayout
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private var position = 0
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var btnAnim: Animation
    private lateinit var tvSkip: TextView
    private var first: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUp()

        setContentView(R.layout.activity_intro)

        btnNext = findViewById(R.id.btn_next)
        btnPrev = findViewById(R.id.btn_prev)
        login = findViewById(R.id.btn_get_started_login)
        register = findViewById(R.id.btn_get_started_register)
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        tvSkip = findViewById(R.id.tv_skip)

        val mList = ArrayList<ScreenItem>()
        mList.add(
            ScreenItem(
                "Make friends",
                "It's never been easier to make friends from worldwide universities.",
                R.drawable.logoonboarding
            )
        )
        mList.add(
            ScreenItem(
                "Connect",
                "Find your university and start following the students life and all the news about it.",
                R.drawable.universitesonboarding
            )
        )
        mList.add(
            ScreenItem(
                "Share",
                "Share your student life with friends and others or go leave a like or comment on friends post",
                R.drawable.travelonboarding
            )
        )
        mList.add(
            ScreenItem(
                "Talk",
                "Start a conversation with a friend or a group",
                R.drawable.textonboarding
            )
        )

        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager!!.adapter = introViewPagerAdapter

        tabIndicator.setupWithViewPager(screenPager)

        btnNext.setOnClickListener {
            position = screenPager!!.currentItem
            if (position < mList.size) {
                position++
                screenPager!!.currentItem = position
            }

            if (position == mList.size - 1) {
                loadLastScreen()
            }
        }

        btnPrev.setOnClickListener {
            position = screenPager!!.currentItem
            if (position < mList.size) {
                position--
                screenPager!!.currentItem = position
                tabIndicator.visibility = View.VISIBLE
            }
        }

        btnPrev.visibility = View.INVISIBLE

        tabIndicator.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    if (p0.position == mList.size - 1) {
                        loadLastScreen()
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    if(p0.position == 0){
                        btnPrev.visibility = View.INVISIBLE
                        btnNext.visibility = View.VISIBLE
                    }else if(p0.position != mList.size - 1){
                        btnPrev.visibility = View.VISIBLE
                        btnNext.visibility = View.VISIBLE
                        register.visibility = View.GONE
                        login.visibility = View.GONE
                    }
                }
            }
        })

        login.setOnClickListener {
            startActivity(Intent(applicationContext, LoginRegisterActivity::class.java)
                .putExtra("action", "login"))
        }

        register.setOnClickListener {
            startActivity(Intent(applicationContext, LoginRegisterActivity::class.java)
                .putExtra("action", "register"))
        }

        tvSkip.setOnClickListener {
            screenPager!!.currentItem = mList.size
            loadLastScreen()
        }
    }

    private fun loadLastScreen() {
        btnPrev.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE
        login.visibility = View.VISIBLE
        register.visibility = View.VISIBLE
        tvSkip.visibility = View.INVISIBLE
        tabIndicator.visibility = View.VISIBLE

        if(first){
            login.animation = btnAnim
            register.animation = btnAnim
        }
        first = false
    }

    private fun setUp(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar!!.hide()
    }
}
