package com.jeppsson.appexplore

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("applicationIcon")
fun setApplicationIcon(view: ImageView, packageName: String) {
    val pm = view.context.packageManager
    val icon: Drawable
    try {
        icon = pm.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        return
    }

    view.setImageDrawable(icon)
}
