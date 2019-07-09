package com.slife.slife.BottomNavigation.Universities.View


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    private lateinit var countrySelected: String
    private lateinit var collegeSelected: String


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

        setOnClickListeners()

        return view
    }

    private fun setOnClickListeners(){
        countryAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                collegesAutoComplete.visibility = View.VISIBLE
                countrySelected = parent?.getItemAtPosition(position).toString()
                collegesInCountryListViewModel.getCollegesInCountry(countrySelected)
            }

        collegesAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener{ parent, _, position, _ ->
                collegeSelected = parent?.getItemAtPosition(position).toString()
                collegeViewModel.getCollege(countrySelected, collegeSelected)
            }

    }

    private fun setUpCountryList() {
        countryListObserver = {
            catalogs: CountryList ->
            run {
                this.countryList = catalogs
                countryAutoComplete.setAdapter(ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, this.countryList.countryList))
            }
        }
        countryListStatusObserver = {
            when(it){
                CountryListViewModel.Status.COMPLETE -> {
                    Log.d("Country List Status", "COMPLETE" + countryList.countryList.toString())
                    pb.visibility = View.GONE
                }
                CountryListViewModel.Status.LOADING -> {
                    Log.d("Country List Status", "LOADING")
                    pb.visibility = View.VISIBLE
                }
                CountryListViewModel.Status.FAILED -> Log.d("Country List Status", "FAILED")
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
            run {
                this.collegesInCountryList = collegesInCountry
                collegesAutoComplete.setAdapter(ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, this.collegesInCountryList.collegeList))
            }
        }
        collegesInCountryListStatusObserver = {
            when(it){
                CollegesInCountryViewModel.Status.COMPLETE -> {
                    pb.visibility = View.GONE
                    Log.d("Colleges List Status", "COMPLETE")
                }
                CollegesInCountryViewModel.Status.LOADING -> {
                    pb.visibility = View.VISIBLE
                    Log.d("Colleges List Status", "LOADING")
                }
                CollegesInCountryViewModel.Status.FAILED -> Log.d("Colleges List Status", "FAILED")
            }
        }
        collegesInCountryListViewModel = ViewModelProviders.of(activity!!).get(CollegesInCountryViewModel::class.java)
        collegesInCountryListViewModel.let {
            it.getCollegesInCountry("").observe(viewLifecycleOwner, Observer(collegesInCountryListObserver))
            it.getStatus().observe(viewLifecycleOwner, Observer(collegesInCountryListStatusObserver))
        }
    }

    private fun setUpCollegeObserver(){
        collegeObserver = {
            college: College ->
            run {
                this.college = college
                Toast.makeText(context, this.college.toString(), Toast.LENGTH_LONG).show()
                Log.d("College", this.college.toString())
            }
        }
        collegeStatusObserver = {
            when(it){
                CollegeViewModel.Status.COMPLETE -> {
                    Log.d("College", "COMPLETE")
                    pb.visibility = View.GONE
                }
                CollegeViewModel.Status.LOADING -> {
                    Log.d("College", "LOADING")
                    pb.visibility = View.VISIBLE
                }
                CollegeViewModel.Status.FAILED -> Log.d("College", "FAILED")
            }
        }
        collegeViewModel = ViewModelProviders.of(activity!!).get(CollegeViewModel::class.java)
        collegeViewModel.let {
            it.getCollege("", "").observe(viewLifecycleOwner, Observer(collegeObserver))
            it.getStatus().observe(viewLifecycleOwner, Observer(collegeStatusObserver))
        }
    }
}
