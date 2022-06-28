package com.example.socialmap.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.UserModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.FragmentVerifyNumberBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*


class VerifyNumber : Fragment() {
/*
    private var _binding: FragmentVerifyNumberBinding? = null
    private val binding get() = _binding!!
*/
    private lateinit var binding : FragmentVerifyNumberBinding
    private lateinit var profileViewModels: ProfileViewModel

    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null

    private lateinit var appPermission: AppPermission
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userArrayList: ArrayList<UserModel>

    private var code : String? = null
    private lateinit var pin: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("Code")
        }
        print("oncreate calıstı")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // _binding = FragmentVerifyNumberBinding.inflate(inflater,container,false)


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_verify_number,container,false)


        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)
        userArrayList = arrayListOf<UserModel>()

        FirebaseApp.initializeApp(/*context=*/this.requireContext())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )



        binding.btnVerify.setOnClickListener {
            if(checkPin()){
                val credential = PhoneAuthProvider.getCredential(code!!,pin)
                signInUser(credential)
            }
            print("button tıklandı")
        }

        return binding.root
    }

    private fun signInUser(credential: PhoneAuthCredential) {

        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if(it!!.isSuccessful){
                val  userModel= UserModel(
                    "", "", "",
                    firebaseAuth!!.currentUser!!.phoneNumber!!
                )
                databaseReference!!.child(firebaseAuth?.uid!!).setValue(userModel)

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, GetUserData())
                    .commit()



            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(code: String,) =
            VerifyNumber().apply {
                arguments = Bundle().apply {
                    putString("Code", code)
                }
                print("80.satır")
            }
    }

    private fun checkPin(): Boolean{
        pin= binding.otpTextView.text.toString()
        if(pin.isEmpty()) {
            binding.otpTextView.error = "Alan gereklidir."
            return false
        } else if (pin.length<6){
            binding.otpTextView.error = " Geçerli pini giriniz."
            return false
        }else return true
    }

    private fun getUserData(){
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
                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.main_container, Profile())
                                    .commit()
                            }
                            else{
                                Log.i("asd", "2. çalıştı " + userArrayList.get(x).name)
                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.main_container, GetUserData())
                                    .commit()
                            }
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}