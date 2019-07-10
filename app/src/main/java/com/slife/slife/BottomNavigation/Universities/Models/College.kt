package com.slife.slife.BottomNavigation.Universities.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class College (
    @SerializedName("acronym")
    @Expose
    val acronym: String,

    @SerializedName("addressLocality")
    @Expose
    val addressLocality: String,

    @SerializedName("addressRegion")
    @Expose
    val addressRegion: String,

    @SerializedName("colors")
    @Expose
    val colors: String,

    @SerializedName("country")
    @Expose
    val country: String,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("facebook")
    @Expose
    val facebook: String,

    @SerializedName("fax")
    @Expose
    val fax: String,

    @SerializedName("foundingDate")
    @Expose
    val foundingDate: String,

    @SerializedName("instagram")
    @Expose
    val instagram: String,

    @SerializedName("link")
    @Expose
    val link: String,

    @SerializedName("linkedin")
    @Expose
    val linkedin: String,

    @SerializedName("motto")
    @Expose
    val motto: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("otherlocations")
    @Expose
    val otherlocations: String,

    @SerializedName("postalCode")
    @Expose
    val postalCode: String,

    @SerializedName("streetAddress")
    @Expose
    val streetAddress: String,

    @SerializedName("telephone")
    @Expose
    val telephone: String,

    @SerializedName("twitter")
    @Expose
    val twitter: String,

    @SerializedName("youtube")
    @Expose
    val youtube: String
){
    constructor() : this("","","", "", "", "", "",
        "","","", "", "", "", "",
        "", "", "", "", "", "")
}