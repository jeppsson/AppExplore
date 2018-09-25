package com.jeppsson.appexplore.utils

import android.content.pm.ApplicationInfo
import androidx.annotation.NonNull

internal object Utils {

    fun arrayToLines(strings: Array<String>, defaultString: String): String {
        return arrayToLines(strings, null, defaultString)
    }

    fun arrayToLines(strings: Array<String>?, prefix: String?, defaultString: String): String {
        if (strings == null) {
            return defaultString
        }

        val sb = StringBuilder()

        for (s in strings) {
            sb.append(prefix ?: "").append(s).append('\n')
        }

        if (sb.isEmpty()) {
            return defaultString
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
