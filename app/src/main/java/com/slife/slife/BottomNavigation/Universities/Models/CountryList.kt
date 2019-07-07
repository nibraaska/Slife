package com.slife.slife.BottomNavigation.Universities.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CountryList (
    @SerializedName("CountryList")
    @Expose
    val countryList: ArrayList<String>
)