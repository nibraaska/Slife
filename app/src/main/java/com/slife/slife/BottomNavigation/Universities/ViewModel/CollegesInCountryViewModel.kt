package com.slife.slife.BottomNavigation.Universities.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.Networking.MobileAPI
import com.slife.slife.Networking.MobileCallBack
import com.slife.slife.R

class CollegesInCountryViewModel(application: Application) : AndroidViewModel(application) {
    private val mAPI = MobileAPI(application.resources.getString(R.string.firebase_database_url))
    private var collegesInCountry = MutableLiveData<CollegesInCountry>()
    private var status = MutableLiveData<Status>()

    init {
        collegesInCountry.value = CollegesInCountry(arrayListOf())
    }

    fun getStatus(): LiveData<Status> {
        return status
    }

    fun getCollegeInCountry(country: String): LiveData<CollegesInCountry>{
        status.value = Status.LOADING
        mAPI.getCollegesInCountry(country, object : MobileCallBack<CollegesInCountry>{
            override fun success(data: CollegesInCountry) {
                status.value = Status.COMPLETE
                collegesInCountry.value = data
            }

            override fun failure(message: String) {
                status.value = Status.FAILED
                Log.d("colleges in country", message)
            }

        })
        return collegesInCountry
    }

    enum class Status{
        LOADING,
        COMPLETE,
        FAILED
    }
}