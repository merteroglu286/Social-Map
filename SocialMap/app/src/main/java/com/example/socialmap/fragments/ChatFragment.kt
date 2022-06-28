package com.example.socialmap.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth

class ChatFragment : Fragment() {
/*
    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var appPermission: AppPermission

    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userArrayList: ArrayList<UserModel>
*/
    private lateinit var chatBinding: FragmentChatBinding
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
        _binding = FragmentChatBinding.inflate(inflater,container,false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)
        userArrayList = arrayListOf<UserModel>()

         */

        chatBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat,container,false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            chatBinding.userModel = userModel

            chatBinding.txtProfileName.text = userModel.name

        })









        return chatBinding.root

    }

    /*
    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(userSnapShot in snapshot.children){

                        val user = userSnapShot.getValue(UserModel::class.java)
                        userArrayList.add(user!!)

                    }
                    for (x in userArrayList.indices){
                        if (userArrayList.get(x).uid == firebaseAuth.currentUser!!.uid){
                            Log.i("asd",userArrayList.get(x).uid)
                            break
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

     */

}