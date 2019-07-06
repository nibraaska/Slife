package com.slife.slife.BottomNavigation


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.slife.slife.R


class UniversitiesFragment : Fragment() {

    private lateinit var countryAutoComplete: AutoCompleteTextView
    private lateinit var collegesAutoComplete: AutoCompleteTextView

    private lateinit var pb: ProgressBar

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_universities, container, false)

        countryAutoComplete = v.findViewById(R.id.countryList)
        collegesAutoComplete = v.findViewById(R.id.collegesList)
        pb = v.findViewById(R.id.progressBar)

        getCountryList()

        return v
    }

    @Suppress("UNCHECKED_CAST")
    fun getCountryList(){
        pb.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .child("CountryList")
            .child("CountryList")
            .addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val list: ArrayList<String> = arrayListOf(p0.value)[0] as ArrayList<String>
                    val adapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, list)
                    countryAutoComplete.setAdapter(adapter)
                    pb.visibility = View.GONE
                    countryAutoComplete.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, _, position, _ ->
                            collegesAutoComplete.visibility = View.VISIBLE
                            getCollegesList(parent.getItemAtPosition(position).toString())
                        }
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCollegesList(country: String) {
        pb.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .child("Colleges")
            .child(country)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val map: Map<*, *> = p0.value as Map<*, *>
                    val list: Array<Any?> = map.keys.toTypedArray()
                    val adapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, list)
                    pb.visibility = View.GONE
                    collegesAutoComplete.setAdapter(adapter)
                    collegesAutoComplete.onItemClickListener =
                        AdapterView.OnItemClickListener { parent, _, position, _ ->
                            getCollege(country, parent?.getItemAtPosition(position).toString())
                        }
                }
            })
    }

    private fun getCollege(country: String, college: String) {
        Log.d("here", "$country, $college")
        pb.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference
            .child("Colleges")
            .child(country)
            .child(college)
            .addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    pb.visibility = View.GONE
                    val map: Map<*, *> = p0.value as Map<*, *>
                    Log.d("here", map["acronym"].toString())
                }
            })
    }
}
