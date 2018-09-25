package com.jeppsson.appexplore.utils

import android.content.pm.ApplicationInfo
import androidx.annotation.NonNull

internal object Utils {

    fun arrayToLines(strings: Array<String>): String {
        return arrayToLines(strings, null)
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

    fun metaData(@NonNull applicationInfo: ApplicationInfo): String? {
        val metaData = applicationInfo.metaData
        if (metaData != null) {
            val sb = StringBuilder()
            for (metaDataName in metaData.keySet()) {
                sb.append(metaDataName)
                        .append(": ")
                        .append(metaData.get(metaDataName))
                        .append('\n')
            }
            return sb.toString().trim()
        }

        return null
    }
}
