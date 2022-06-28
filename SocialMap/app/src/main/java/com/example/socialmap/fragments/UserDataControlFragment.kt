package com.example.socialmap.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.activities.GetUserData
import com.example.socialmap.databinding.FragmentUserDataControlBinding
import com.google.firebase.auth.FirebaseAuth


class UserDataControlFragment : Fragment() {
    /*
    private var _binding: FragmentUserDataControlBinding? = null
    private val binding get() = _binding!!


    private lateinit var appPermission: AppPermission

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userArrayList: ArrayList<UserModel>
*/
    private lateinit var binding: FragmentUserDataControlBinding
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var appPermission: AppPermission

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*
        _binding = FragmentUserDataControlBinding.inflate(inflater, container, false)


        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)
        userArrayList = arrayListOf<UserModel>()
*/
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_data_control,container,false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            binding.userModel = userModel

            binding.txtProfileName.text = userModel.name
            binding.txtProfileName.isVisible = false

            if (binding.txtProfileName.text == ""){
                startActivity(Intent(context, GetUserData::class.java))
                requireActivity().finish()
            }else{
                startActivity(Intent(context, DashBoard::class.java))
                requireActivity().finish()
            }

        })

        return binding.root
    }



    private fun getUserData(){
        /*

        databaseReference!!.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(UserModel::class.java)
                        userArrayList.add(user!!)

                    }
                    for (x in userArrayList.indices){
                        if (userArrayList.get(x).uid == firebaseAuth!!.currentUser!!.uid){

                            if (userArrayList.get(x).name == ""){
                                Log.i("asd", "1. çalıştı " + userArrayList.get(x).name)
                                startActivity(Intent(context, DashBoard::class.java))
                                requireActivity().finish()
                            }
                            else{
                                Log.i("asd", "2. çalıştı " + userArrayList.get(x).name)

                                startActivity(Intent(context, GetUserData::class.java))
                                requireActivity().finish()
                            }
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

         */
    }
}