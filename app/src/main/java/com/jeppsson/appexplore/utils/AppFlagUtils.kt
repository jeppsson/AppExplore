package com.jeppsson.appexplore.utils

import android.content.pm.ApplicationInfo

object AppFlagUtils {

    internal fun getReadableFlags(appFlags: Int): String {
        val sb = StringBuilder()

        if (appFlags and ApplicationInfo.FLAG_SYSTEM != 0) {
            sb.append("FLAG_SYSTEM\n")
        }

        if (appFlags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            sb.append("FLAG_DEBUGGABLE\n")
        }

        if (appFlags and ApplicationInfo.FLAG_HAS_CODE != 0) {
            sb.append("FLAG_HAS_CODE\n")
        }

        if (appFlags and ApplicationInfo.FLAG_PERSISTENT != 0) {
            sb.append("FLAG_PERSISTENT\n")
        }

        if (appFlags and ApplicationInfo.FLAG_FACTORY_TEST != 0) {
            sb.append("FLAG_FACTORY_TEST\n")
        }

        if (appFlags and ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING != 0) {
            sb.append("FLAG_ALLOW_TASK_REPARENTING\n")
        }

        if (appFlags and ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA != 0) {
            sb.append("FLAG_ALLOW_CLEAR_USER_DATA\n")
        }

        if (appFlags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0) {
            sb.append("FLAG_UPDATED_SYSTEM_APP\n")
        }

        if (appFlags and ApplicationInfo.FLAG_TEST_ONLY != 0) {
            sb.append("FLAG_TEST_ONLY\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS != 0) {
            sb.append("FLAG_SUPPORTS_SMALL_SCREENS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS != 0) {
            sb.append("FLAG_SUPPORTS_NORMAL_SCREENS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS != 0) {
            sb.append("FLAG_SUPPORTS_LARGE_SCREENS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS != 0) {
            sb.append("FLAG_RESIZEABLE_FOR_SCREENS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES != 0) {
            sb.append("FLAG_SUPPORTS_SCREEN_DENSITIES\n")
        }

        if (appFlags and ApplicationInfo.FLAG_VM_SAFE_MODE != 0) {
            sb.append("FLAG_VM_SAFE_MODE\n")
        }

        if (appFlags and ApplicationInfo.FLAG_ALLOW_BACKUP != 0) {
            sb.append("FLAG_ALLOW_BACKUP\n")
        }

        if (appFlags and ApplicationInfo.FLAG_KILL_AFTER_RESTORE != 0) {
            sb.append("FLAG_KILL_AFTER_RESTORE\n")
        }

        if (appFlags and ApplicationInfo.FLAG_RESTORE_ANY_VERSION != 0) {
            sb.append("FLAG_RESTORE_ANY_VERSION\n")
        }

        if (appFlags and ApplicationInfo.FLAG_EXTERNAL_STORAGE != 0) {
            sb.append("FLAG_EXTERNAL_STORAGE\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS != 0) {
            sb.append("FLAG_SUPPORTS_XLARGE_SCREENS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_LARGE_HEAP != 0) {
            sb.append("FLAG_LARGE_HEAP\n")
        }

        if (appFlags and ApplicationInfo.FLAG_STOPPED != 0) {
            sb.append("FLAG_STOPPED\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUPPORTS_RTL != 0) {
            sb.append("FLAG_SUPPORTS_RTL\n")
        }

        if (appFlags and ApplicationInfo.FLAG_INSTALLED != 0) {
            sb.append("FLAG_INSTALLED\n")
        }

        if (appFlags and ApplicationInfo.FLAG_IS_DATA_ONLY != 0) {
            sb.append("FLAG_IS_DATA_ONLY\n")
        }

        if (appFlags and ApplicationInfo.FLAG_IS_GAME != 0) {
            sb.append("FLAG_IS_GAME\n")
        }

        if (appFlags and ApplicationInfo.FLAG_FULL_BACKUP_ONLY != 0) {
            sb.append("FLAG_FULL_BACKUP_ONLY\n")
        }

        if (appFlags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0) {
            sb.append("FLAG_USES_CLEARTEXT_TRAFFIC\n")
        }

        if (appFlags and ApplicationInfo.FLAG_EXTRACT_NATIVE_LIBS != 0) {
            sb.append("FLAG_EXTRACT_NATIVE_LIBS\n")
        }

        if (appFlags and ApplicationInfo.FLAG_HARDWARE_ACCELERATED != 0) {
            sb.append("FLAG_HARDWARE_ACCELERATED\n")
        }

        if (appFlags and ApplicationInfo.FLAG_SUSPENDED != 0) {
            sb.append("FLAG_SUSPENDED\n")
        }

        if (appFlags and ApplicationInfo.FLAG_MULTIARCH != 0) {
            sb.append("FLAG_MULTIARCH\n")
        }

        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
        }

        return sb.toString()
    }
}
