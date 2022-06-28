package com.example.socialmap.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.socialmap.LocationModel
import com.example.socialmap.Repository.LocationRepo

class LocationViewModel: ViewModel() {

    private var appRepo = LocationRepo.StaticFun.getInstance()

    fun getLocation(): LiveData<LocationModel> {
        return appRepo.getLocation()
    }

    fun updateLatiude(lat: String) {
        appRepo.updateLatitude(lat)

    }

    fun updateLongitude(lng: String?) {
        appRepo.updateLongatude(lng!!)
    }

    fun updateImage(imagePath: String) {
        appRepo.updateImage(imagePath)
    }
}