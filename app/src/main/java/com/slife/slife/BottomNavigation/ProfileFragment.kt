package com.slife.slife.BottomNavigation


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.slife.slife.MainActivity
import com.slife.slife.R
import com.slife.slife.onboarding.LoginRegisterActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    private lateinit var v: View

    private lateinit var logoutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_profile, container, false)
        logoutBtn = v.findViewById(R.id.logoutBtn)

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)?.edit()?.putBoolean("openedBefore", false)
                ?.apply()
            startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("action", "login"))
        }
        return v
    }
}
