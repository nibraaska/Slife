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
        val myEmail = email.text.toString()
        val myPassword = password.text.toString()
        val mycPassword = confirmPassword.text.toString()
        val myName = name.text.toString()

        if(myName.isEmpty()){
            name.error = "Please enter your name"
            name.requestFocus()
        }
        else if(myEmail.isEmpty()){
            email.error = "Please enter your email"
            email.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(myEmail).matches()){
            email.error = "Please enter a valid email"
            email.requestFocus()
        }
        else if(myPassword.isEmpty()){
            password.error = "Please enter your password"
            password.requestFocus()
        }
        else if(mycPassword.isEmpty()){
            confirmPassword.error = "Please enter your password again"
            confirmPassword.requestFocus()
        }
        else if(myPassword != mycPassword){
            confirmPassword.error = "Passwords need to match"
            confirmPassword.requestFocus()
        } else {
            pb.visibility = View.VISIBLE
            createUser()
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
                    Toast.makeText(this@RegisterActivity, "createUserWithEmail:", Toast.LENGTH_SHORT).show()
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