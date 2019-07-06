package com.slife.slife.BottomNavigation


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.*
import com.slife.slife.R
import kotlinx.android.synthetic.main.fragment_universities.*

class UniversitiesFragment : Fragment() {

    private lateinit var countryListRef: DatabaseReference
    private lateinit var collegesRef: DatabaseReference

    private lateinit var countryMenuText: TextView
    private lateinit var countryMenu: PopupMenu

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_universities, container, false)

        countryMenuText = v.findViewById(R.id.universitiesList)
        countryMenu = PopupMenu(this.context, countryMenuText)
        countryMenuText.setOnClickListener {
            countryMenu.show()
        }

        return v
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        countryListRef = FirebaseDatabase.getInstance().reference
            .child("CountryList")
            .child("CountryList")

        collegesRef = FirebaseDatabase.getInstance().reference
            .child("Colleges")

        countryListRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val list: ArrayList<String> = arrayListOf(p0.value)[0] as ArrayList<String>
                for (country in list){
                    countryMenu.menu.add(country)
                }
                countryMenu.setOnMenuItemClickListener {
                    collegesRef.child(it.toString())
                        .addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}

                            override fun onDataChange(p0: DataSnapshot) {
                                Log.d("here", p0.toString())
                            }
                        })
                    true
                }
            }
        })
    }
}
