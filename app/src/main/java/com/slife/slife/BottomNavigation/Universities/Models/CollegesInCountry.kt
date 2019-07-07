package com.slife.slife.BottomNavigation.Universities.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CollegesInCountry (
    @SerializedName("Colleges")
    @Expose
    val collegeList: ArrayList<String>
)