package com.jeppsson.appexplore.databinding

import android.content.pm.ApplicationInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.io.File

@BindingAdapter("shared_libraries")
fun sharedLibraries(view: TextView, applicationInfo: ApplicationInfo) {
    if (applicationInfo.sharedLibraryFiles != null) {
        view.text = arrayToLines(applicationInfo.sharedLibraryFiles, null)
    }
}

@BindingAdapter("native_libraries")
fun nativeLibraries(view: TextView, applicationInfo: ApplicationInfo) {
    if (applicationInfo.nativeLibraryDir != null) {
        view.text = arrayToLines(File(applicationInfo.nativeLibraryDir).list(),
                applicationInfo.nativeLibraryDir)
    }
}

fun arrayToLines(strings: Array<String>?, prefix: String?): String {
    val sb = StringBuilder()

    if (strings != null) {
        for (s in strings) {
            sb.append(prefix ?: "").append(s).append('\n')
        }
    }

    return sb.toString().trim()
}
