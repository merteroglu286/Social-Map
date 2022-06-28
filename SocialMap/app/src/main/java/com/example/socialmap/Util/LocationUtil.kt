package com.example.socialmap.Util

import com.google.firebase.auth.FirebaseAuth

class LocationUtil {
    fun getUID(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid
    }
}