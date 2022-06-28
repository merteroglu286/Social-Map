package com.example.socialmap.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.socialmap.Adapter.MapInfoAdapter
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.LocationModel
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.LocationViewModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.databinding.DialogLayoutBinding
import com.example.socialmap.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.min


class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {


/*
    private val callback = OnMapReadyCallback { googleMap ->
        val ankara = LatLng(39.9208372, 32.8454561)
        googleMap.addMarker(MarkerOptions().position(ankara).title(name))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara,15f))
    }
*/
    private lateinit var mMap : GoogleMap

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    private lateinit var profileViewModels: ProfileViewModel

    private lateinit var appPermission: AppPermission

    private lateinit var binding: FragmentMapsBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReference: DatabaseReference

    private lateinit var name : String
    private lateinit var image : String
    private lateinit var status : String
    private lateinit var myMarker: Marker
    private var locationArrayList : ArrayList<LatLng>? = null


    val etkinlik1 = LatLng(41.011752, 28.782395)
    val etkinlik2 = LatLng(41.011654, 28.774788)
    val etkinlik3 = LatLng(41.005003, 28.773638)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_maps,container,false)

/*
        lifecycleScope.launch {
            Log.e("TAG123",getBitmap().toString())
        }


 */
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Location")
        sharedPreferences = requireContext()!!.getSharedPreferences("userData", Context.MODE_PRIVATE)


        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)

        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            binding.userModel = userModel
            name = userModel.name
            status = userModel.status
            image = userModel.image
        })



        locationArrayList = ArrayList()

        locationArrayList!!.add(etkinlik1)
        locationArrayList!!.add(etkinlik2)
        locationArrayList!!.add(etkinlik3)


    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0

        try {
            var success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.mapstyle)
            )
            if (!success){
                Log.e("TagMaps","Map stili yuklenirken hata olustu")
            }
        }catch (e: Exception){
            Log.e("TagMaps","Duzgun yuklendi")
        }
/*
        val ankara = LatLng(39.9208372, 32.8454561)
        mMap.addMarker(MarkerOptions().position(ankara).title(name))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara,15f))
 */


        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(konum: Location) {
                // lokasyon,konum degisince yapilacak islemer
                mMap.clear()
                val guncelKonum = LatLng(konum.latitude,konum.longitude)

/*
                myMarker = mMap.addMarker(MarkerOptions().position(guncelKonum).title(name).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))!!

 */
                Glide.with(requireContext())
                    .asBitmap()
                    .load(image)
                    .into(object : CustomTarget<Bitmap>(96,96){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            myMarker = mMap.addMarker(MarkerOptions().position(guncelKonum).title(name).icon(
                                getCroppedBitmap(resource)?.let {
                                    BitmapDescriptorFactory.fromBitmap(
                                        it
                                    )
                                }))!!
                            myMarker.tag=0

                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                            // this is called when imageView is cleared on lifecycle call or for
                            // some other reason.
                            // if you are referencing the bitmap somewhere else too other than this imageView
                            // clear it here as you can no longer have the bitmap
                        }
                    })


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                mMap.addMarker(MarkerOptions().position(locationArrayList!!.get(0)).title("etkinlik 1").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.joystick24)
                ))
                mMap.addMarker(MarkerOptions().position(locationArrayList!!.get(1)).title("etkinlik 2").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.party36)
                ))
                mMap.addMarker(MarkerOptions().position(locationArrayList!!.get(2)).title("etkinlik 3").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.joystick48)
                ))


                mMap.setOnMarkerClickListener(this@MapsFragment)

                mMap.uiSettings.isZoomGesturesEnabled = false

            }

            override fun onProviderDisabled(@NonNull provider: String) {

            }

            override fun onProviderEnabled(@NonNull provider: String) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),AppConstants.LOCATION_PERMISSION)
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == AppConstants.LOCATION_PERMISSION){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMarkerClick(mymarker: Marker): Boolean {

        sendData(mymarker.position.latitude.toString(),mymarker.position.longitude.toString(),image)
        return false
    }



    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    private fun getCircularBitmap(srcBitmap: Bitmap?): Bitmap {
        val squareBitmapWidth = min(srcBitmap!!.width, srcBitmap.height)
        // Initialize a new instance of Bitmap
        // Initialize a new instance of Bitmap
        val dstBitmap = Bitmap.createBitmap(
            squareBitmapWidth,  // Width
            squareBitmapWidth,  // Height
            Bitmap.Config.ARGB_8888 // Config
        )
        val canvas = Canvas(dstBitmap)
        // Initialize a new Paint instance
        // Initialize a new Paint instance
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
        val rectF = RectF(rect)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        // Calculate the left and top of copied bitmap
        // Calculate the left and top of copied bitmap
        val left = ((squareBitmapWidth - srcBitmap.width) / 2).toFloat()
        val top = ((squareBitmapWidth - srcBitmap.height) / 2).toFloat()
        canvas.drawBitmap(srcBitmap, left, top, paint)
        // Free the native object associated with this bitmap.
        // Free the native object associated with this bitmap.
        srcBitmap.recycle()
        // Return the circular bitmap
        // Return the circular bitmap
        return dstBitmap
    }

    private fun sendData(lat: String, lng: String, image: String) = kotlin.run {
        val map = mapOf(
            "latitude" to lat,
            "longitude" to lng,
            "imageUrl" to image,
            "uid" to firebaseAuth!!.uid!!.toString(),
        )
        LocationModel(lat,lng,image,firebaseAuth!!.uid!!.toString())
        databaseReference!!.child(firebaseAuth!!.uid!!.toString()).updateChildren(map)
    }
}

