package com.jeppsson.appexplore

import android.content.pm.PackageManager
import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

@BindingAdapter("visibleGone")
fun showGone(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

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
