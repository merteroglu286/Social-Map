package com.example.socialmap.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.EditNameActivity
import com.example.socialmap.activities.MainActivity
import com.example.socialmap.databinding.DialogLayoutBinding
import com.example.socialmap.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class Profile : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var dialogLayoutBinding : DialogLayoutBinding
    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var dialog:  AlertDialog
    private lateinit var appPermission: AppPermission

    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        profileBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)

        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            profileBinding.userModel = userModel

            if(userModel.name.contains("")){
                val split = userModel.name.split("")

                profileBinding.txtProfileFName.text = split[0]
                profileBinding.txtProfileLName.text = split[1]
            }

            profileBinding.cardName.setOnClickListener {
                val intent = Intent(context,EditNameActivity::class.java)
                intent.putExtra("name", userModel.name)
                startActivityForResult(intent,100)
            }
        })

        profileBinding.imgPickImage.setOnClickListener{
            if(appPermission.isStorageOk(requireContext())){
                pickImage()
            }
        }

        profileBinding.imgEditStatus.setOnClickListener {
            getStatusDialog()
        }

        profileBinding.logOut.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }

        return profileBinding.root
    }

    private fun pickImage() {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }

    private fun getStatusDialog() {

        val alertDialog = AlertDialog.Builder(context)
        dialogLayoutBinding = DialogLayoutBinding.inflate(layoutInflater)
        alertDialog.setView(dialogLayoutBinding.root)

        dialogLayoutBinding.btnEditStatus.setOnClickListener {
            val status = dialogLayoutBinding.edtUserStatus.text.toString()
            if (status.isNotEmpty()) {
                profileViewModels.updateStatus(status)
                dialog.dismiss()
            }
        }
        dialog = alertDialog.create()
        dialog.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (data != null) {
                    val userName = data.getStringExtra("name")
                    profileViewModels.updateName(userName!!)
                    val editor = sharedPreferences.edit()
                    editor.putString("myName", userName).apply()
                }

            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (data != null) {

                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        uploadImage(result.uri)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.STORAGE_PERMISSION -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {

        storageReference = FirebaseStorage.getInstance().reference
        storageReference.child(firebaseAuth.uid + AppConstants.PATH).putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val task = taskSnapshot.storage.downloadUrl
                task.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val imagePath = it.result.toString()

                        val editor = sharedPreferences.edit()
                        editor.putString("myImage", imagePath).apply()

                        profileViewModels.updateImage(imagePath)
                    }
                }
            }
    }

}