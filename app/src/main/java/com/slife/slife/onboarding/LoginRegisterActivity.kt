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
import com.slife.slife.MainActivity
import com.slife.slife.R


class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginSignUp: TextView
    private lateinit var loginPb: ProgressBar

    private lateinit var registerBtn: Button
    private lateinit var registerName: EditText
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerConfirmPassword: EditText
    private lateinit var registerPb: ProgressBar
    private lateinit var registerSignIn: TextView

    private lateinit var loginStub: ViewStub
    private lateinit var loginView: View
    private lateinit var registerStub: ViewStub
    private lateinit var registerView: View

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseConfig()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginStub = findViewById(R.id.login_stub)
        loginStub.layoutResource = R.layout.layout_login
        loginView = loginStub.inflate()
        setUpLogin(loginView)

        registerStub = findViewById(R.id.register_stub)
        registerStub.layoutResource = R.layout.layout_register
        registerView = registerStub.inflate()
        setUpRegister(registerView)

        when (intent.getStringExtra("action")) {
            "login" -> {
                registerView.visibility = View.GONE
            }
            "register" -> {
                loginView.visibility = View.GONE
            }
        }
    }

    private fun baseConfig() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar!!.hide()
    }

    private fun setUpLogin(inflatedView: View) {
        inflatedView.let {
            loginBtn = it.findViewById(R.id.cirLoginButton)
            loginEmail = it.findViewById(R.id.emailLogin)
            loginPassword = it.findViewById(R.id.passwordLogin)
            loginSignUp = it.findViewById(R.id.needsAccount)
            loginPb = it.findViewById(R.id.progressLogin)
        }
        loginBtn.setOnClickListener {
            checkFieldsAndLogin()
        }
        loginSignUp.setOnClickListener {
            loginView.visibility = View.GONE
            registerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRegister(inflatedView: View) {
        inflatedView.let {
            registerBtn = it.findViewById(R.id.registerBtn)
            registerName = it.findViewById(R.id.nameRegister)
            registerEmail = it.findViewById(R.id.emailRegister)
            registerPassword = it.findViewById(R.id.passwordRegister)
            registerConfirmPassword = it.findViewById(R.id.passwordRegisterConfirm)
            registerPb = it.findViewById(R.id.progressRegister)
            registerSignIn = it.findViewById(R.id.alreadyCreated)
        }
        registerBtn.setOnClickListener {
            checkFieldsAndCreateUser()
        }
        registerSignIn.setOnClickListener {
            registerView.visibility = View.GONE
            loginView.visibility = View.VISIBLE
        }
    }

    private fun checkFieldsAndLogin() {

        val email = loginEmail.text.toString().trim()
        val password = loginPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                loginEmail.error = "Please enter your Email"
                loginEmail.requestFocus()
            }
            password.isEmpty() -> {
                loginPassword.error = "Please enter your Password"
                loginPassword.requestFocus()
            }
            else -> {
                login()
            }
        }
    }

    private fun checkFieldsAndCreateUser() {

        val name = registerName.text.toString().trim()
        val email = registerEmail.text.toString().trim()
        val password = registerPassword.text.toString().trim()
        val cPassword = registerConfirmPassword.text.toString().trim()

        when {
            name.isEmpty() -> {
                registerName.error = "Please enter your Name"
                registerName.requestFocus()
            }
            email.isEmpty() -> {
                registerEmail.error = "Please enter your Email"
                registerEmail.requestFocus()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                registerEmail.error = "Please enter a valid Email"
                registerEmail.requestFocus()
            }
            !email.endsWith("edu") -> {
                registerEmail.error = "Please enter your school email"
                registerEmail.requestFocus()
            }
            password.isEmpty() -> {
                registerPassword.error = "Please enter your Password"
                registerPassword.requestFocus()
            }
            cPassword.isEmpty() -> {
                registerConfirmPassword.error = "Please enter your Password again"
                registerConfirmPassword.requestFocus()
            }
            password != cPassword -> {
                registerConfirmPassword.error = "Passwords need to match"
                registerConfirmPassword.requestFocus()
            }
            else -> {
                registerPb.visibility = View.VISIBLE
                createUser()
            }
        }
    }

    private fun login() {
        loginPb.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(loginEmail.text.toString(), loginPassword.text.toString())
            .addOnCompleteListener { p0 ->
                loginPb.visibility = View.GONE
                if (p0.isSuccessful) {
                    savePrefsData()
                    Toast.makeText(this, "Logged in as ${loginEmail.text}", Toast.LENGTH_SHORT).show()
                    mainActivityIntent()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUser() {
        registerPb.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(registerEmail.text.toString(), registerPassword.text.toString())
            .addOnCompleteListener(
                this
            ) { p0 ->
                registerPb.visibility = View.GONE
                if (p0.isSuccessful) {
                    savePrefsData()
                    Toast.makeText(this, "createUserWithEmail", Toast.LENGTH_SHORT).show()
                    mainActivityIntent()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun mainActivityIntent(){
        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun savePrefsData() {
        applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE).edit().putBoolean("openedBefore", true).apply()
    }
}