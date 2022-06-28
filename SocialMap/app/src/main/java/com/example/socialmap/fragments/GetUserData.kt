package com.example.socialmap.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.activities.DashBoard
import com.example.socialmap.databinding.FragmentGetUserDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class GetUserData : Fragment() {

    private var _binding: FragmentGetUserDataBinding? = null
    private val binding get() = _binding!!

    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private lateinit var imageUrl: String
    private lateinit var uid: String

    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGetUserDataBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().reference


        binding.btnDataDone.setOnClickListener {
            if (checkData()) {
                uploadData(username, status, image!!)
            }
        }

        binding.imgPickImage.setOnClickListener {
            if (checkStoragePermission())
                pickImage()
            else storageRequestPermission()
        }


        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkData(): Boolean {
        username = binding.edtUserName.text.toString().trim()
        status = binding.edtUserStatus.text.toString().trim()

        if(username.isEmpty()) {
            binding.edtUserName.error = "Filed is required"
            return false
        } else if (status.isEmpty()) {
            binding.edtUserStatus.error = "Filed is required"
            return false
        } else if (image == null) {
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return false

        } else return true
    }


    private fun uploadData(name: String, status: String, image: Uri) = kotlin.run {
        storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH).putFile(image)
            .addOnSuccessListener {
                val task = it.storage.downloadUrl
                task.addOnCompleteListener { uri ->
                    imageUrl = uri.result.toString()
                    uid = firebaseAuth!!.uid!!.toString()
                    val map = mapOf(
                        "name" to name,
                        "status" to status,
                        "image" to imageUrl,
                        "uid" to uid,
                        "email" to firebaseAuth!!.currentUser!!.email
                    )
                    databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)

                    startActivity(Intent(context, DashBoard::class.java))
                    requireActivity().finish()
                }
            }
    }
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1000
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    image = result.uri
                    binding.imgUser.setImageURI(image)
                }
            }
        }
    }

    private fun pickImage() {
        context?.let {
            CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(it, this)
        }
    }
}