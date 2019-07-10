package com.slife.slife.Networking

import com.slife.slife.BottomNavigation.Universities.Models.College
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Endpoints {
    @GET("CountryList/.json")
    fun getCountryList(): Call<CountryList>

    @GET("CollegesInCountry/{country}/.json")
    fun getCollegesInCountry(@Path("country") country: String): Call<CollegesInCountry>

    @GET("Colleges/{country}/{college}/.json")
    fun getCollege(@Path("country") country: String, @Path("college") college: String): Call<College>
}