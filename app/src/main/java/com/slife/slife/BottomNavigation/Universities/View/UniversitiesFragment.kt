package com.slife.slife.BottomNavigation.Universities.View


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.slife.slife.BottomNavigation.Universities.Models.College
import com.slife.slife.BottomNavigation.Universities.Models.CollegesInCountry
import com.slife.slife.BottomNavigation.Universities.Models.CountryList
import com.slife.slife.BottomNavigation.Universities.ViewModel.CollegeViewModel
import com.slife.slife.BottomNavigation.Universities.ViewModel.CollegesInCountryViewModel
import com.slife.slife.BottomNavigation.Universities.ViewModel.CountryListViewModel
import com.slife.slife.R


class UniversitiesFragment : Fragment() {

    private lateinit var countryAutoComplete: AutoCompleteTextView
    private lateinit var collegesAutoComplete: AutoCompleteTextView
    private lateinit var pb: ProgressBar

    private lateinit var countryList: CountryList
    private lateinit var countryListViewModel: CountryListViewModel
    private lateinit var countryListObserver: ((CountryList) -> Unit)
    private lateinit var countryListStatusObserver: ((CountryListViewModel.Status) -> Unit)

    private lateinit var collegesInCountryList: CollegesInCountry
    private lateinit var collegesInCountryListViewModel: CollegesInCountryViewModel
    private lateinit var collegesInCountryListObserver: ((CollegesInCountry) -> Unit)
    private lateinit var collegesInCountryListStatusObserver: ((CollegesInCountryViewModel.Status) -> Unit)

    private lateinit var college: College
    private lateinit var collegeViewModel: CollegeViewModel
    private lateinit var collegeObserver: ((College) -> Unit)
    private lateinit var collegeStatusObserver: ((CollegeViewModel.Status) -> Unit)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_universities, container, false)
        view.let {
            countryAutoComplete = it.findViewById(R.id.countryList)
            collegesAutoComplete = it.findViewById(R.id.collegesList)
            pb = it.findViewById(R.id.progressBar)
        }

        setUpCountryList()
        setUpCollegesInCountryList()
        setUpCollegeObserver()

        collegesInCountryListViewModel.getCollegeInCountry("United States")
        collegeViewModel.getCollege("United States", "Middle Tennessee State University")

        return view
    }

    private fun setUpCountryList() {
        countryListObserver = {
            catalogs: CountryList ->
            run { this.countryList = catalogs }
        }
        countryListStatusObserver = {
            when(it){
                CountryListViewModel.Status.COMPLETE -> Log.d("Country List Status", countryList.countryList.toString())
                CountryListViewModel.Status.LOADING -> Log.d("Country List Status", "loading")
                CountryListViewModel.Status.FAILED -> Log.d("Country List Status", "failed")
            }
        }
        countryListViewModel = ViewModelProviders.of(activity!!).get(CountryListViewModel::class.java)
        countryListViewModel.let {
            it.getCountryList().observe(viewLifecycleOwner, Observer(countryListObserver))
            it.getStatus().observe(viewLifecycleOwner, Observer(countryListStatusObserver))
        }
    }

    private fun setUpCollegesInCountryList(){
        collegesInCountryListObserver = {
            collegesInCountry: CollegesInCountry ->
            run { this.collegesInCountryList = collegesInCountry }
        }
        collegesInCountryListStatusObserver = {
            when(it){
                CollegesInCountryViewModel.Status.COMPLETE -> Log.d("Colleges List Status", this.collegesInCountryList.collegeList.toString())
                CollegesInCountryViewModel.Status.LOADING -> Log.d("Colleges List Status", "loading")
                CollegesInCountryViewModel.Status.FAILED -> Log.d("Colleges List Status", "failed")
            }
        }
        collegesInCountryListViewModel = ViewModelProviders.of(activity!!).get(CollegesInCountryViewModel::class.java)
        collegesInCountryListViewModel.let {
            it.getCollegeInCountry("").observe(viewLifecycleOwner, Observer(collegesInCountryListObserver))
            it.getStatus().observe(viewLifecycleOwner, Observer(collegesInCountryListStatusObserver))
        }
    }

    private fun setUpCollegeObserver(){
        collegeObserver = {
            college: College ->
            run { this.college = college }
        }
        collegeStatusObserver = {
            when(it){
                CollegeViewModel.Status.COMPLETE -> Log.d("College", this.college.toString())
                CollegeViewModel.Status.LOADING -> Log.d("College", "loading")
                CollegeViewModel.Status.FAILED -> Log.d("College", "failed")
            }
        }
        collegeViewModel = ViewModelProviders.of(activity!!).get(CollegeViewModel::class.java)
        collegeViewModel.let {
            it.getCollege("", "").observe(viewLifecycleOwner, Observer(collegeObserver))
            it.getStatus().observe(viewLifecycleOwner, Observer(collegeStatusObserver))
        }
    }
}
