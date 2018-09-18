package com.jeppsson.appexplore.utils

import android.content.pm.FeatureInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

internal object FeaturesUtils {
    fun getFeatures(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.reqFeatures != null) {
            for (featureInfo in packageInfo.reqFeatures) {
                if (featureInfo.name == null) {
                    sb.append(reqGlEsVersionToString(featureInfo))
                } else {
                    sb.append(featureInfo.name)
                            .append(" (")
                            .append(featureFlagsToString(featureInfo))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        sb.append(featureVersionToString(featureInfo))
                    }
                    sb.append(')')
                }
                sb.append('\n')
            }
        }

        return sb.toString().trim { it <= ' ' }
    }

    private fun featureFlagsToString(featureInfo: FeatureInfo): String {
        return "required=" + (featureInfo.flags and FeatureInfo.FLAG_REQUIRED != 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun featureVersionToString(featureInfo: FeatureInfo): String {
        return if (featureInfo.version > 0) ", version=" + featureInfo.version else ""
    }

    private fun reqGlEsVersionToString(featureInfo: FeatureInfo): String {
        return if (featureInfo.name == null) "reqGlEsVersion=" + featureInfo.glEsVersion else ""
    }

    fun getFeaturesNoAvailable(packageManager: PackageManager, packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.reqFeatures != null) {
            for (featureInfo in packageInfo.reqFeatures) {
                if (featureInfo.name != null) {
                    if (!packageManager.hasSystemFeature(featureInfo.name)) {
                        sb.append(featureInfo.name)
                                .append(" (")
                                .append(featureFlagsToString(featureInfo))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            sb.append(featureVersionToString(featureInfo))
                        }
                        sb.append(')')
                    }
                }
                sb.append('\n')
            }
        }

        return sb.toString().trim { it <= ' ' }
    }
}
