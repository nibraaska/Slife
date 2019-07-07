package com.slife.slife.BottomNavigation.Universities.View


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.slife.slife.BottomNavigation.Universities.Models.College
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import com.slife.slife.Networking.MobileAPI
import com.slife.slife.Networking.MobileCallBack
import com.slife.slife.R


class UniversitiesFragment : Fragment() {

    private lateinit var countryAutoComplete: AutoCompleteTextView
    private lateinit var collegesAutoComplete: AutoCompleteTextView

    private lateinit var pb: ProgressBar

    private lateinit var v: View

    private lateinit var mAPI: MobileAPI


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_universities, container, false)

        mAPI = MobileAPI(resources.getString(R.string.BASE_URL))

        countryAutoComplete = v.findViewById(R.id.countryList)
        collegesAutoComplete = v.findViewById(R.id.collegesList)
        pb = v.findViewById(R.id.progressBar)

        mAPI.getCountryList(object : MobileCallBack<CountryList> {
            override fun success(data: CountryList) {
                Log.d("vm", data.countryList[0])
            }
            override fun failure(message: String) {
                Log.d("vm", message)
            }
        })

        mAPI.getCollegesInCountry("Morocco", object : MobileCallBack<CollegesInCountry> {
            override fun success(data: CollegesInCountry) {
                Log.d("vm", data.collegeList.toString())
            }

            override fun failure(message: String) {
                Log.d("vm", message)
            }
        })

        mAPI.getCollege("Morocco", "Universite Cadi Ayyad", object : MobileCallBack<College> {
            override fun success(data: College) {
                Log.d("vm", data.toString())
            }

            override fun failure(message: String) {
                Log.d("vm", message)
            }

        })
        return v
    }
}
