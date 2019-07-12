package com.slife.slife.BottomNavigation.Profile

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import com.slife.slife.BottomNavigation.Universities.ViewModel.CollegesInCountryViewModel
import com.slife.slife.BottomNavigation.Universities.ViewModel.CountryListViewModel
import com.slife.slife.R
import com.slife.slife.onboarding.LoginRegisterActivity
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {

    private lateinit var profilePicture: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var countryAutoComplete: AutoCompleteTextView
    private lateinit var collegeAutoComplete: AutoCompleteTextView
    private lateinit var updateBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var pb: ProgressBar

    private lateinit var userID: String

    private lateinit var imageUri: Uri
    private val IMAGE_CAPTURE_CODE = 1001
    private var bitmap: Bitmap? = null
    private var outputStream = ByteArrayOutputStream()
    private lateinit var encodeByte: ByteArray

    private lateinit var countryList: CountryList
    private lateinit var countryListViewModel: CountryListViewModel
    private lateinit var countryListObserver: ((CountryList) -> Unit)
    private lateinit var countryListStatusObserver: ((CountryListViewModel.Status) -> Unit)

    private lateinit var collegesInCountryList: CollegesInCountry
    private lateinit var collegesInCountryListViewModel: CollegesInCountryViewModel
    private lateinit var collegesInCountryListObserver: ((CollegesInCountry) -> Unit)
    private lateinit var collegesInCountryListStatusObserver: ((CollegesInCountryViewModel.Status) -> Unit)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.let {
            profilePicture = it.findViewById(R.id.profilePictureFragment)
            nameEditText = it.findViewById(R.id.nameFragment)
            countryAutoComplete = it.findViewById(R.id.countryFragment)
            collegeAutoComplete = it.findViewById(R.id.collegeFragment)
            updateBtn = it.findViewById(R.id.updateBtn)
            logoutBtn = it.findViewById(R.id.logoutBtn)
            pb = it.findViewById(R.id.profileFragmentPb)
        }

        setUpCountryList()
        setUpCollegesInCountryList()

        userID = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("Users").child(userID).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.childrenCount > 0){
                    val map = p0.value as Map<*, *>
                    Glide.with(activity?.application).load(map["profileImage"].toString()).into(profilePicture)
                    nameEditText.setText(map["Name"].toString())
                    countryAutoComplete.setText(map["Country"].toString())
                    collegeAutoComplete.setText(map["College"].toString())
                    pb.visibility = View.GONE
                }
            }
        })

        profilePicture.setOnClickListener {
            openCamera()
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            this.context?.let { it1 ->
                AuthUI.getInstance()
                    .signOut(it1)
                    .addOnCompleteListener {
                        context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)?.edit()?.putBoolean("openedBefore", false)
                            ?.apply()
                        startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("action", "login"))
                    }
            }
        }

        updateBtn.setOnClickListener {
            checkFieldsAndSetAccount()
        }


        countryAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener{ parent, _, position, _ ->
                val countrySelected = parent?.getItemAtPosition(position).toString()
                collegesInCountryListViewModel.getCollegesInCountry(countrySelected)
                collegeAutoComplete.setText("")
            }

        return view
    }

    private fun setUpCountryList() {
        countryListObserver = {
                catalogs: CountryList ->
            run {
                this.countryList = catalogs
                countryAutoComplete.setAdapter(ArrayAdapter(this@ProfileFragment.context!!, android.R.layout.simple_dropdown_item_1line, this.countryList.countryList))
            }
        }
        countryListStatusObserver = {
            when(it){
                CountryListViewModel.Status.COMPLETE -> {
                    Log.d("Country List Status", "COMPLETE" + countryList.countryList.toString())
                }
                CountryListViewModel.Status.LOADING -> {
                    Log.d("Country List Status", "LOADING")
                    pb.visibility = View.VISIBLE
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
                collegeAutoComplete.setAdapter(ArrayAdapter(this@ProfileFragment.context!!, android.R.layout.simple_dropdown_item_1line, this.collegesInCountryList.collegeList))
            }
        }
        collegesInCountryListStatusObserver = {
            when(it){
                CollegesInCountryViewModel.Status.COMPLETE -> {
                    Log.d("Colleges List Status", "COMPLETE")
                }
                CollegesInCountryViewModel.Status.LOADING -> {
                    pb.visibility = View.VISIBLE
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
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            profilePicture.setImageURI(imageUri)
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
        }
    }

    private fun checkFieldsAndSetAccount(){
        val name = nameEditText.text.toString().trim()
        val country = countryAutoComplete.text.toString().trim()
        val college = collegeAutoComplete.text.toString().trim()

        when {
            bitmap == null -> {
                profilePicture.requestFocus()
            }
            name.isEmpty() -> {
                nameEditText.error = "Please enter your name"
                nameEditText.requestFocus()
            }
            country.isEmpty() -> {
                countryAutoComplete.error = "Please choose your country"
                countryAutoComplete.requestFocus()
            }
            college.isEmpty() -> {
                collegeAutoComplete.error = "Please choose your college"
                collegeAutoComplete.requestFocus()
            }
            else -> {
                pb.visibility = View.VISIBLE
                saveData()
                pb.visibility = View.GONE
            }
        }
    }

    private fun saveData() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userID!!)
        ref.let {
            it.child("Name").setValue(nameEditText.text.toString())
            it.child("Country").setValue(countryAutoComplete.text.toString())
            it.child("College").setValue(collegeAutoComplete.text.toString())
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
}
