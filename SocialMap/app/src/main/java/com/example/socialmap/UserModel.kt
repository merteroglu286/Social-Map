package com.example.socialmap

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class UserModel(
    var name: String = "",
    val status: String = "",
    val image: String = "",
    var number: String = "",
    val uid: String = "",
    val email : String = ""
){


    companion object{
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view:CircleImageView,imageUrl:String?){
            imageUrl?.let {
                Glide.with(view.context).load(imageUrl).into(view)
            }
        }
    }
}
