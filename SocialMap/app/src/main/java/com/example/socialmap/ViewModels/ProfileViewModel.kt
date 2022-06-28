package com.example.socialmap.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.socialmap.Repository.AppRepo
import com.example.socialmap.UserModel

class ProfileViewModel: ViewModel() {

    private var appRepo = AppRepo.StaticFun.getInstance()

    fun getUser(): LiveData<UserModel> {
        return appRepo.getUser()
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)

    }

    fun updateName(userName: String?) {
        appRepo.updateName(userName!!)
    }

    fun updateImage(imagePath: String) {
        appRepo.updateImage(imagePath)
    }
}