package com.slife.slife.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.slife.slife.R


class RegisterActivity: AppCompatActivity() {

    private lateinit var viewStub: ViewStub
    private lateinit var inflatedView: View

    private lateinit var auth: FirebaseAuth

    private lateinit var registerBtn: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var pb: ProgressBar
    private lateinit var alreadyCreated: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUp()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        viewStub = findViewById(R.id.layout_stub)
        viewStub.layoutResource = R.layout.layout_register
        inflatedView = viewStub.inflate()

        inflatedView.let {
            registerBtn = it.findViewById(R.id.registerBtn)
            name = it.findViewById(R.id.nameRegister)
            email = it.findViewById(R.id.emailRegister)
            password = it.findViewById(R.id.passwordRegister)
            confirmPassword = it.findViewById(R.id.passwordRegisterConfirm)
            pb = it.findViewById(R.id.progressRegister)
            alreadyCreated = it.findViewById(R.id.alreadyCreated)
        }

        registerBtn.setOnClickListener {
            checkFieldsAndCreate()
        }

        alreadyCreated.setOnClickListener {
            val loginActivity = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginActivity)
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

    private fun checkFieldsAndCreate(){
        when {
            name.text.toString().isEmpty() -> {
                name.error = "Please enter your name"
                name.requestFocus()
            }
            email.text.toString().isEmpty() -> {
                email.error = "Please enter your email"
                email.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() -> {
                email.error = "Please enter a valid email"
                email.requestFocus()
            }
            password.text.toString().isEmpty() -> {
                password.error = "Please enter your password"
                password.requestFocus()
            }
            confirmPassword.text.toString().isEmpty() -> {
                confirmPassword.error = "Please enter your password again"
                confirmPassword.requestFocus()
            }
            password.text.toString() != confirmPassword.text.toString() -> {
                confirmPassword.error = "Passwords need to match"
                confirmPassword.requestFocus()
            }
            else -> {
                pb.visibility = View.VISIBLE
                createUser()
            }
        }
    }

    private fun createUser(){
        pb.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this
            ) { p0 ->
                pb.visibility = View.GONE
                if (p0.isSuccessful) {
                    savePrefsData()
                    Toast.makeText(this@RegisterActivity, "createUserWithEmail", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        pref.edit().putBoolean("openedBefore", true).apply()
    }
}