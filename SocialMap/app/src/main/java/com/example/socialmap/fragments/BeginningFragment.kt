package com.example.socialmap.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmap.R
import com.example.socialmap.SignInActivity
import com.example.socialmap.activities.MainActivity
import com.example.socialmap.databinding.FragmentBeginningBinding
import com.example.socialmap.databinding.FragmentGetUserDataBinding


class BeginningFragment : Fragment() {

    private lateinit var _binding: FragmentBeginningBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeginningBinding.inflate(inflater, container, false)

        binding.btnEmail.setOnClickListener {
            val intent = Intent(this.context, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnPhone.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.main_container, GetNumber())
                ?.commit()
        }



        return binding.root
    }

}