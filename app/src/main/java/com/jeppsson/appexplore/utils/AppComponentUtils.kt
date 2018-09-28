package com.jeppsson.appexplore.utils

import android.content.pm.PackageInfo
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

internal object AppComponentUtils {

    private const val LAUNCH_MULTIPLE = 0
    private const val LAUNCH_SINGLE_TOP = 1
    private const val LAUNCH_SINGLE_TASK = 2
    private const val LAUNCH_SINGLE_INSTANCE = 3

    fun getActivities(packageInfo: PackageInfo): Spannable {
        val sb = StringBuilder()

        if (packageInfo.activities != null) {
            for (activityInfo in packageInfo.activities) {
                sb.append(activityInfo.name).append('\n')
                val info = StringBuilder()
                if (activityInfo.exported) {
                    info.append("(exported)").append(' ')
                }
                if (activityInfo.launchMode != LAUNCH_MULTIPLE) {
                    info.append('(')
                            .append(launchMode(activityInfo.launchMode))
                            .append(')')
                            .append(' ')
                }
                if (info.isNotEmpty()) {
                    info.append('\n')
                }
                sb.append(info)
            }
        } else {
            sb.append("No activities")
        }

        return colorize(sb.toString().trim())
    }

    fun getServices(packageInfo: PackageInfo): Spannable {
        val sb = StringBuilder()

        if (packageInfo.services != null) {
            for (serviceInfo in packageInfo.services) {
                sb.append(serviceInfo.name).append('\n')
                if (serviceInfo.exported) {
                    sb.append("(exported)").append('\n')
                }
            }
        } else {
            sb.append("No services")
        }

        return colorize(sb.toString().trim())
    }

    fun getContentProviders(packageInfo: PackageInfo): Spannable {
        val sb = StringBuilder()

        if (packageInfo.providers != null) {
            for (providerInfo in packageInfo.providers) {
                sb.append(providerInfo.name).append('\n')
                if (providerInfo.exported) {
                    sb.append("(exported)").append('\n')
                }
            }
        } else {
            sb.append("No content providers")
        }

        return colorize(sb.toString().trim())
    }

    fun getReceivers(packageInfo: PackageInfo): Spannable {
        val sb = StringBuilder()

        if (packageInfo.receivers != null) {
            for (activityInfo in packageInfo.receivers) {
                sb.append(activityInfo.name).append('\n')
                if (activityInfo.exported) {
                    sb.append("(exported)").append('\n')
                }
            }
        } else {
            sb.append("No receivers")
        }

        return colorize(sb.toString().trim())
    }


    private fun launchMode(launchMode: Int): String {
        return when (launchMode) {
            LAUNCH_MULTIPLE -> "standard"
            LAUNCH_SINGLE_TOP -> "singleTop"
            LAUNCH_SINGLE_TASK -> "singleTask"
            LAUNCH_SINGLE_INSTANCE -> "singleInstance"
            else -> "unknown"
        }
    }

    private fun colorize(text: String): SpannableString {
        val content = SpannableString(text)

        val m = Pattern.compile("\\b(?:exported)\\b").matcher(text)
        while (m.find()) {
            content.setSpan(ForegroundColorSpan(Color.YELLOW),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return content
    }
}
