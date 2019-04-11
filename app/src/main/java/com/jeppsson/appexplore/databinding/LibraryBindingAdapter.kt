package com.jeppsson.appexplore.databinding

import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.io.File
import java.lang.ref.WeakReference

@BindingAdapter("shared_libraries")
fun sharedLibraries(view: TextView, applicationInfo: ApplicationInfo) {
    if (applicationInfo.sharedLibraryFiles != null) {
        view.text = arrayToLines(applicationInfo.sharedLibraryFiles, null)
    }
}

@BindingAdapter("native_libraries")
fun nativeLibraries(view: TextView, applicationInfo: ApplicationInfo) {
    if (applicationInfo.nativeLibraryDir != null) {
        NativeLibraryDir(WeakReference(view), applicationInfo.nativeLibraryDir)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
}

private fun arrayToLines(strings: Array<String>?, prefix: String?): String {
    val sb = StringBuilder()

    if (strings != null) {
        for (s in strings) {
            sb.append(prefix ?: "").append(s).append('\n')
        }
    }

    return sb.toString().trim()
}

private class NativeLibraryDir(
    private val view: WeakReference<TextView>,
    private val nativeLibraryDir: String
) : AsyncTask<Void, Void, Array<String>?>() {

    override fun doInBackground(vararg params: Void?): Array<String>? {
        return File(nativeLibraryDir).list()
    }

    override fun onPostExecute(result: Array<String>?) {
        view.get()?.text = arrayToLines(result, nativeLibraryDir)
    }
}
