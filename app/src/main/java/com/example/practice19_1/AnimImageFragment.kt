package com.example.practice19_1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment

class AnimImageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_anim_image, container, false)
        val image = view.findViewById<ImageView>(R.id.imageForLoaded)
        val animation = AnimationUtils.loadAnimation(context, R.anim.logo_rotate_animation)
        image.startAnimation(animation)
        return view
    }
}