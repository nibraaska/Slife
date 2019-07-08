package com.slife.slife.BottomNavigation.Universities.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import com.slife.slife.Networking.MobileAPI
import com.slife.slife.Networking.MobileCallBack
import com.slife.slife.R

class CountryListViewModel(application: Application) : AndroidViewModel(application) {
    private val mAPI = MobileAPI(application.resources.getString(R.string.firebase_database_url))
    private var countryList = MutableLiveData<CountryList>()
    private var status = MutableLiveData<Status>()

    init {
        countryList.value = CountryList(arrayListOf())
    }

    fun getStatus(): LiveData<Status> {
        return status
    }

    fun getCountryList(): LiveData<CountryList>{
        status.value = Status.LOADING
        mAPI.getCountryList(object : MobileCallBack<CountryList> {
            override fun success(data: CountryList) {
                status.value = Status.COMPLETE
                countryList.value = data
            }
            override fun failure(message: String) {
                status.value = Status.FAILED
                Log.d("vm", message)
            }
        })
        return countryList
    }

    enum class Status{
        LOADING,
        COMPLETE,
        FAILED
    }
}