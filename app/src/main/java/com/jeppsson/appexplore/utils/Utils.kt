package com.jeppsson.appexplore.utils

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
}
