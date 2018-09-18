package com.jeppsson.appexplore.utils

import android.content.pm.PackageInfo

internal object AppComponentUtils {

    private const val LAUNCH_MULTIPLE = 0
    private const val LAUNCH_SINGLE_TOP = 1
    private const val LAUNCH_SINGLE_TASK = 2
    private const val LAUNCH_SINGLE_INSTANCE = 3

    fun getActivities(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.activities != null) {
            for (activityInfo in packageInfo.activities) {
                sb.append(activityInfo.name).append('\n')
                if (activityInfo.exported) {
                    sb.append("Exported: true").append('\n')
                }
                if (activityInfo.launchMode != LAUNCH_MULTIPLE) {
                    sb.append("LaunchMode: ")
                            .append(launchMode(activityInfo.launchMode))
                            .append('\n')
                }
            }
        } else {
            sb.append("No activities")
        }

        return sb.toString().trim { it <= ' ' }
    }

    fun getServices(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.services != null) {
            for (serviceInfo in packageInfo.services) {
                sb.append(serviceInfo.name).append('\n')
                        .append("Exported: ").append(serviceInfo.exported).append('\n')
            }
        } else {
            sb.append("No services")
        }

        return sb.toString().trim { it <= ' ' }
    }

    fun getContentProviders(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.providers != null) {
            for (providerInfo in packageInfo.providers) {
                sb.append(providerInfo.name).append('\n')
                        .append("Exported: ").append(providerInfo.exported).append('\n')
            }
        } else {
            sb.append("No content providers")
        }

        return sb.toString().trim { it <= ' ' }
    }

    fun getReceivers(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.receivers != null) {
            for (activityInfo in packageInfo.receivers) {
                sb.append(activityInfo.name).append('\n')
                        .append("Exported: ").append(activityInfo.exported).append('\n')
            }
        } else {
            sb.append("No receivers")
        }

        return sb.toString().trim { it <= ' ' }
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
}
