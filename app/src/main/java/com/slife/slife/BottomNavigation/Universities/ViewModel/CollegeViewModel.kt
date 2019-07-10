package com.slife.slife.BottomNavigation.Universities.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.slife.slife.BottomNavigation.Universities.Models.College
import com.slife.slife.Networking.MobileAPI
import com.slife.slife.Networking.MobileCallBack
import com.slife.slife.R

class CollegeViewModel(application: Application) : AndroidViewModel(application) {
    private val mAPI = MobileAPI(application.resources.getString(R.string.firebase_database_url))
    private var collegeObj = MutableLiveData<College>()
    private var status = MutableLiveData<Status>()

    init {
        collegeObj.value = College()
    }

    fun getStatus(): LiveData<Status> {
        return status
    }

    fun getCollege(country: String, college: String): LiveData<College>{
        status.value = Status.LOADING
        mAPI.getCollege(country, college, object : MobileCallBack<College>{
            override fun success(data: College) {
                status.value = Status.COMPLETE
                collegeObj.value = data
            }

            override fun failure(message: String) {
                status.value = Status.FAILED
                Log.d("colleges in country", message)
            }

        })
        return collegeObj
    }

    enum class Status{
        LOADING,
        COMPLETE,
        FAILED
    }
}