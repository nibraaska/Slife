package com.slife.slife.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.slife.slife.R

class LoginActivity: AppCompatActivity() {

    private lateinit var Loginbutton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var needsAccount: TextView
    private lateinit var pb: ProgressBar

    private lateinit var viewStub: ViewStub
    private lateinit var inflatedView: View

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUp()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        viewStub = findViewById(R.id.layout_stub)
        viewStub.layoutResource = R.layout.layout_login
        inflatedView = viewStub.inflate()

        inflatedView.let {
            Loginbutton = it.findViewById(R.id.cirLoginButton)
            email = it.findViewById(R.id.emailLogin)
            password = it.findViewById(R.id.passwordLogin)
            needsAccount = it.findViewById(R.id.needsAccount)
            pb = it.findViewById(R.id.progressLogin)
        }

        Loginbutton.setOnClickListener {
            checkFieldsAndLogin()
        }

        needsAccount.setOnClickListener {
            val registerActivity = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(registerActivity)
            finish()
        }

    }

    private fun setUp(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar!!.hide()
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        pref.edit().putBoolean("openedBefore", true).apply()
    }

    private fun checkFieldsAndLogin(){
        when {
            email.text.toString().isEmpty() -> {
                email.error = "Please enter your email"
                email.requestFocus()
            }
            password.text.toString().isEmpty() -> {
                password.error = "Please enter your password"
                password.requestFocus()
            }
            else -> {
                login()
            }
        }
    }

    private fun login(){
        pb.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener { p0 ->
                pb.visibility = View.GONE
                if (p0.isSuccessful) {
                    savePrefsData()
                    Toast.makeText(this@LoginActivity, "Logged in as ${email.text}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}