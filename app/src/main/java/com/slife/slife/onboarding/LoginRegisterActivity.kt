package com.slife.slife.onboarding

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewStub
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import com.slife.slife.BottomNavigation.Universities.ViewModel.CollegesInCountryViewModel
import com.slife.slife.BottomNavigation.Universities.ViewModel.CountryListViewModel
import com.slife.slife.MainActivity
import com.slife.slife.R
import java.io.ByteArrayOutputStream


class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginSignUp: TextView
    private lateinit var loginPb: ProgressBar

    private lateinit var registerBtn: Button
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerConfirmPassword: EditText
    private lateinit var registerPb: ProgressBar
    private lateinit var registerSignIn: TextView

    private lateinit var pictureSetUp: ImageView
    private lateinit var nameSetUp: EditText
    private lateinit var countrySetUp: AutoCompleteTextView
    private lateinit var collegeSetUp: AutoCompleteTextView
    private lateinit var continueBtn: Button
    private lateinit var setUpPb: ProgressBar

    private lateinit var loginStub: ViewStub
    private lateinit var loginView: View
    private lateinit var registerStub: ViewStub
    private lateinit var registerView: View
    private lateinit var setUpStub: ViewStub
    private lateinit var setUpView: View

    private lateinit var auth: FirebaseAuth

    private lateinit var imageUri: Uri
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var bitmap: Bitmap? = null
    private var outputStream = ByteArrayOutputStream()

    private lateinit var countryList: CountryList
    private lateinit var countryListViewModel: CountryListViewModel
    private lateinit var countryListObserver: ((CountryList) -> Unit)
    private lateinit var countryListStatusObserver: ((CountryListViewModel.Status) -> Unit)

    private lateinit var collegesInCountryList: CollegesInCountry
    private lateinit var collegesInCountryListViewModel: CollegesInCountryViewModel
    private lateinit var collegesInCountryListObserver: ((CollegesInCountry) -> Unit)
    private lateinit var collegesInCountryListStatusObserver: ((CollegesInCountryViewModel.Status) -> Unit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseConfig()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginStub = findViewById(R.id.login_stub)
        loginStub.layoutResource = R.layout.layout_login
        loginView = loginStub.inflate()
        setUpLogin(loginView)
        loginView.visibility = View.GONE

        registerStub = findViewById(R.id.register_stub)
        registerStub.layoutResource = R.layout.layout_register
        registerView = registerStub.inflate()
        setUpRegister(registerView)
        registerView.visibility = View.GONE

        setUpStub = findViewById(R.id.setUp_stub)
        setUpStub.layoutResource = R.layout.layout_account_set_up
        setUpView = setUpStub.inflate()
        setUpSetUp(setUpView)
        setUpView.visibility = View.GONE


        when (intent.getStringExtra("action")) {
            "login" -> {
                loginView.visibility = View.VISIBLE
            }
            "register" -> {
                registerView.visibility = View.VISIBLE
            }
        }

        setUpCountryList()
        setUpCollegesInCountryList()
    }

    private fun setUpSetUp(inflatedView: View) {
        inflatedView.let {
            pictureSetUp = it.findViewById(R.id.profilePicture)
            nameSetUp = it.findViewById(R.id.nameSetUp)
            countrySetUp = it.findViewById(R.id.countrySetUp)
            collegeSetUp = it.findViewById(R.id.collegesSetUp)
            continueBtn = it.findViewById(R.id.setUpContinue)
            setUpPb = it.findViewById(R.id.progressSetUp)
        }

        pictureSetUp.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        }

        countrySetUp.onItemClickListener =
            AdapterView.OnItemClickListener{ parent, _, position, _ ->
                val countrySelected = parent?.getItemAtPosition(position).toString()
                collegesInCountryListViewModel.getCollegesInCountry(countrySelected)
        }

        continueBtn.setOnClickListener {
            checkFieldsAndSetAccount()
        }
    }

    private fun setUpCountryList() {
        countryListObserver = {
                catalogs: CountryList ->
            run {
                this.countryList = catalogs
                countrySetUp.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, this.countryList.countryList))
            }
        }
        countryListStatusObserver = {
            when(it){
                CountryListViewModel.Status.COMPLETE -> {
                    Log.d("Country List Status", "COMPLETE" + countryList.countryList.toString())
                    setUpPb.visibility = View.GONE
                }
                CountryListViewModel.Status.LOADING -> {
                    Log.d("Country List Status", "LOADING")
                    setUpPb.visibility = View.VISIBLE
                }
                CountryListViewModel.Status.FAILED -> Log.d("Country List Status", "FAILED")
            }
        }
        countryListViewModel = ViewModelProviders.of(this).get(CountryListViewModel::class.java)
        countryListViewModel.let {
            it.getCountryList().observe(this, Observer(countryListObserver))
            it.getStatus().observe(this, Observer(countryListStatusObserver))
        }
    }

    private fun setUpCollegesInCountryList(){
        collegesInCountryListObserver = {
                collegesInCountry: CollegesInCountry ->
            run {
                this.collegesInCountryList = collegesInCountry
                collegeSetUp.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, this.collegesInCountryList.collegeList))
            }
        }
        collegesInCountryListStatusObserver = {
            when(it){
                CollegesInCountryViewModel.Status.COMPLETE -> {
                    setUpPb.visibility = View.GONE
                    Log.d("Colleges List Status", "COMPLETE")
                }
                CollegesInCountryViewModel.Status.LOADING -> {
                    setUpPb.visibility = View.VISIBLE
                    Log.d("Colleges List Status", "LOADING")
                }
                CollegesInCountryViewModel.Status.FAILED -> Log.d("Colleges List Status", "FAILED")
            }
        }
        collegesInCountryListViewModel = ViewModelProviders.of(this).get(CollegesInCountryViewModel::class.java)
        collegesInCountryListViewModel.let {
            it.getCollegesInCountry("").observe(this, Observer(collegesInCountryListObserver))
            it.getStatus().observe(this, Observer(collegesInCountryListStatusObserver))
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.let {
            it.put(MediaStore.Images.Media.TITLE, "Profile Picture")
            it.put(MediaStore.Images.Media.DESCRIPTION, "Profile Picture")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            pictureSetUp.setImageURI(imageUri)
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
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

    private fun checkFieldsAndSetAccount(){
        val name = nameSetUp.text.toString().trim()
        val country = countrySetUp.text.toString().trim()
        val college = collegeSetUp.text.toString().trim()

        when {
            bitmap == null -> {
                pictureSetUp.requestFocus()
            }
            name.isEmpty() -> {
                nameSetUp.error = "Please enter your name"
                nameSetUp.requestFocus()
            }
            country.isEmpty() -> {
                countrySetUp.error = "Please choose your country"
                countrySetUp.requestFocus()
            }
            college.isEmpty() -> {
                collegeSetUp.error = "Please choose your college"
                countrySetUp.requestFocus()
            }
            else -> {
                setUpPb.visibility = View.VISIBLE
                saveData()
                setUpPb.visibility = View.GONE
                mainActivityIntent()
            }
        }
    }

    private fun saveData() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!)
        ref.let {
            it.child("Name").setValue(nameSetUp.text.toString())
            it.child("Country").setValue(countrySetUp.text.toString())
            it.child("College").setValue(collegeSetUp.text.toString())
        }
        val filePath = FirebaseStorage.getInstance().getReference("profile_images").child(userID)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val data = outputStream.toByteArray()
        filePath.putBytes(data).addOnSuccessListener {
            filePath.downloadUrl.addOnSuccessListener {
                ref.child("profileImage").setValue(it.toString())
            }
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

        val email = registerEmail.text.toString().trim()
        val password = registerPassword.text.toString().trim()
        val cPassword = registerConfirmPassword.text.toString().trim()

        when {
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
                    registerView.visibility = View.GONE
                    setUpView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun mainActivityIntent() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun savePrefsData() {
        applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE).edit().putBoolean("openedBefore", true)
            .apply()
    }
}