package com.jeppsson.appexplore.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

internal object PermissionsUtils {

    fun getPermissions(packageManager: PackageManager, packageInfo: PackageInfo): Spannable {
        val sb = StringBuilder()

        // todo: Check packageInfo.permissions and packageInfo.requestedPermissionsFlags

        if (packageInfo.requestedPermissions != null) {
            for (permissionName in packageInfo.requestedPermissions) {
                val permissionInfo: PermissionInfo
                try {
                    permissionInfo = packageManager.getPermissionInfo(permissionName,
                            PackageManager.GET_META_DATA)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    continue
                }

                sb.append(permissionInfo.name)
                        .append(" (")
                        .append(protectionToString(permissionInfo.protectionLevel))
                        .append(")\n")
            }
        }

        return colorize(sb.toString().trim { it <= ' ' })
    }

    fun getPermissionsNotGranted(packageInfo: PackageInfo): String {
        val sb = StringBuilder()

        if (packageInfo.requestedPermissionsFlags != null) {
            for (i in packageInfo.requestedPermissionsFlags.indices) {
                if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED == 0) {
                    sb.append(packageInfo.requestedPermissions[i]).append('\n')
                }
            }
        }

        return sb.toString().trim { it <= ' ' }
    }


    private fun protectionToString(level: Int): String {
        var protLevel = "????"
        when (level and PermissionInfo.PROTECTION_MASK_BASE) {
            PermissionInfo.PROTECTION_DANGEROUS -> protLevel = "dangerous"
            PermissionInfo.PROTECTION_NORMAL -> protLevel = "normal"
            PermissionInfo.PROTECTION_SIGNATURE -> protLevel = "signature"
            PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM -> protLevel = "signatureOrSystem"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_PRIVILEGED != 0) {
            protLevel += "|privileged"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_DEVELOPMENT != 0) {
            protLevel += "|development"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_APPOP != 0) {
            protLevel += "|appop"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_PRE23 != 0) {
            protLevel += "|pre23"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_INSTALLER != 0) {
            protLevel += "|installer"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_VERIFIER != 0) {
            protLevel += "|verifier"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_PREINSTALLED != 0) {
            protLevel += "|preinstalled"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_SETUP != 0) {
            protLevel += "|setup"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_INSTANT != 0) {
            protLevel += "|instant"
        }
        if (level and PermissionInfo.PROTECTION_FLAG_RUNTIME_ONLY != 0) {
            protLevel += "|runtime"
        }
        return protLevel
    }

    private fun colorize(text: String): SpannableString {
        val content = SpannableString(text)

        var m = Pattern.compile("\\b(?:dangerous)\\b").matcher(text)
        while (m.find()) {
            content.setSpan(ForegroundColorSpan(Color.CYAN),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        m = Pattern.compile("\\b(?:signature)\\b").matcher(text)
        while (m.find()) {
            content.setSpan(ForegroundColorSpan(Color.YELLOW),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        m = Pattern.compile("\\b(?:signatureOrSystem)\\b").matcher(text)
        while (m.find()) {
            content.setSpan(ForegroundColorSpan(Color.RED),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return content
    }
}