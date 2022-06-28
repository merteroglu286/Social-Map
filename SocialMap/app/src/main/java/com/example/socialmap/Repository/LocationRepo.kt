package com.example.socialmap.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialmap.LocationModel
import com.example.socialmap.Util.LocationUtil
import com.google.firebase.database.*

class LocationRepo {

    private var liveData : MutableLiveData<LocationModel>? = null
    private lateinit var databaseReference: DatabaseReference
    private val locationUtil = LocationUtil()

    object StaticFun{
        private var instance : LocationRepo? = null
        fun getInstance():LocationRepo{
            if (instance==null)
                instance = LocationRepo()

            return instance!!
        }
    }

    fun getLocation(): LiveData<LocationModel> {
        if (liveData==null)
            liveData = MutableLiveData()

        databaseReference = FirebaseDatabase.getInstance().getReference("Location").child(locationUtil.getUID()!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val locationModel = snapshot.getValue(LocationModel::class.java)
                    liveData!!.postValue(locationModel)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return liveData!!
    }

    fun updateLatitude(lat: String?) {


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Location").child(locationUtil.getUID()!!)

        val map = mapOf<String, Any>("latiude" to lat!!)
        databaseReference.updateChildren(map)

    }

    fun updateLongatude(lng: String) {

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Location").child(locationUtil.getUID()!!)

        val map = mapOf<String, Any>("longitude" to lng)
        databaseReference.updateChildren(map)

    }

    fun updateImage(imagePath: String) {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("Location").child(locationUtil.getUID()!!)

        val map = mapOf<String, Any>("image" to imagePath)
        databaseReference.updateChildren(map)
    }
}