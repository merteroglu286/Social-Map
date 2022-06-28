package com.example.socialmap.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.example.socialmap.NetworkConnection
import com.example.socialmap.R
import com.example.socialmap.Util.AppUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        firebaseAuth = FirebaseAuth.getInstance()
        appUtil = AppUtil()

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )



        Handler().postDelayed({

            if (firebaseAuth!!.currentUser == null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result!!
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(appUtil.getUID()!!)

                            val map: MutableMap<String, Any> = HashMap()
                            map["token"] = token
                            databaseReference.updateChildren(map)
                        }
                    })
                startActivity(Intent(this, DashBoard::class.java))
                finish()
            }

        }, 2000)

    }
}