package com.slife.slife.BottomNavigation.Messages.View


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.slife.slife.BottomNavigation.Messages.Adapter.UserAutoCompleteAdapter
import com.slife.slife.BottomNavigation.Messages.Model.UserItem
import com.slife.slife.R


class MessagesFragment : Fragment() {

    private lateinit var userAutoCompleteTextView: AutoCompleteTextView
    private var userList: ArrayList<UserItem> = ArrayList()
    private lateinit var userAdapter: UserAutoCompleteAdapter

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_messages, container, false)
        userAutoCompleteTextView = v.findViewById(R.id.userAutoComplete)

        getUsers()


        return v
    }

    private fun getUsers() {
        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists() && p0.childrenCount > 0){
                    p0.children.forEach {
                        if (it.key.toString() != FirebaseAuth.getInstance().currentUser?.uid) {
                            val user =
                                UserItem(it.child("Name").value.toString(), it.child("profileImage").value.toString())
                            userList.add(user)
                        }
                    }
                    userAdapter =
                        UserAutoCompleteAdapter(context!!, userList)
                    userAutoCompleteTextView.setAdapter(userAdapter)
                }
            }

        })
    }


}
