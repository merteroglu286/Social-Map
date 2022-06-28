package com.example.socialmap.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.socialmap.R
import com.example.socialmap.databinding.ActivityDashBoardBinding
import com.example.socialmap.fragments.ChatFragment
import com.example.socialmap.fragments.ContactFragment
import com.example.socialmap.fragments.MapsFragment
import com.example.socialmap.fragments.Profile

class DashBoard : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardBinding

    private var fragment : Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment()).commit()
            binding.bottomChip.setItemSelected(R.id.btnChat)
        }
        binding.bottomChip.setOnItemSelectedListener { id ->
            when (id) {
                R.id.btnChat -> {
                    fragment = ChatFragment()


                }

                R.id.btnProfile -> {
                    fragment = Profile();

                }

                R.id.btnContact -> fragment = ContactFragment()
                R.id.btnMap -> fragment = MapsFragment()

            }

            fragment!!.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment!!)
                    .commit()
            }
        }
    }
}