package com.example.listedecontactsversion2

import android.net.Uri
import android.widget.ImageView

fun ImageView.showContactPhoto(uri: String?) {
    if (uri.isNullOrEmpty()) {
        setImageResource(android.R.drawable.ic_menu_camera)
        return
    }
    try {
        setImageURI(Uri.parse(uri))
    } catch (e: Exception) {
        setImageResource(android.R.drawable.ic_menu_camera)
    }
}
