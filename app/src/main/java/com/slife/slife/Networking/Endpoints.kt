package com.slife.slife.Networking

import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import retrofit2.Call
import retrofit2.http.GET

interface Endpoints {
    @GET("CountryList/.json")
    fun getCountryList(): Call<CountryList>
}