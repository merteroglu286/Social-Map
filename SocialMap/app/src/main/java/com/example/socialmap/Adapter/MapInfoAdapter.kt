package com.example.socialmap.Adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.socialmap.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoAdapter (context: Context): GoogleMap.InfoWindowAdapter {

    private val contentView = (context as Activity).layoutInflater.inflate(R.layout.map_infoview,null)

    override fun getInfoContents(marker: Marker): View? {
        renderView(marker, contentView)
        return contentView
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderView(marker,contentView)
        return contentView

    }


    private fun renderView(marker : Marker? , contentView : View){
        val title = marker?.title
        val description = marker?.snippet

        val titletextView = contentView.findViewById<TextView>(R.id.tvName_mapInfo)
        titletextView.text = title

        val descriptionTextView = contentView.findViewById<TextView>(R.id.tvStatus_mapInfo)
        descriptionTextView.text = description
    }
}