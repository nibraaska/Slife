package com.slife.slife.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.slife.slife.R

class LoginActivity: AppCompatActivity() {

    private lateinit var Loginbutton: Button

    private lateinit var viewStub: ViewStub
    private lateinit var inflatedView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUp()
        setContentView(R.layout.activity_login)

        viewStub = findViewById(R.id.layout_stub)
        viewStub.layoutResource = R.layout.layout_login
        inflatedView = viewStub.inflate()

        Loginbutton = inflatedView.findViewById(R.id.cirLoginButton)

        Loginbutton.setOnClickListener {
            savePrefsData()
        }

    }

    private fun setUp(){
        // Make it full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Hides action bar
        supportActionBar!!.hide()
    }

    // Saves so on boarding doesn't happen again
    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        pref.edit().putBoolean("openedBefore", true).apply()
    }
}