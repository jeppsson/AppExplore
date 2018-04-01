package com.jeppsson.appexplore;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;

class PermissionsUtils {

    static String getPermissions(PackageManager packageManager, PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        // todo: Check packageInfo.permissions and packageInfo.requestedPermissionsFlags

        if (packageInfo.requestedPermissions != null) {
            for (String permissionName : packageInfo.requestedPermissions) {
                PermissionInfo permissionInfo;
                try {
                    permissionInfo = packageManager.getPermissionInfo(permissionName,
                            PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }

                sb.append(permissionInfo.name)
                        .append(" (")
                        .append(protectionToString(permissionInfo.protectionLevel))
                        .append(")\n");
            }
        }

        return sb.toString().trim();
    }

    public static String getPermissionsNotGranted(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.requestedPermissionsFlags != null) {
            for (int i = 0; i < packageInfo.requestedPermissionsFlags.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i]
                        & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
                    sb.append(packageInfo.requestedPermissions[i]).append('\n');
                }
            }
        }

        return sb.toString().trim();
    }


    private static String protectionToString(int level) {
        String protLevel = "????";
        switch (level & PermissionInfo.PROTECTION_MASK_BASE) {
            case PermissionInfo.PROTECTION_DANGEROUS:
                protLevel = "dangerous";
                break;
            case PermissionInfo.PROTECTION_NORMAL:
                protLevel = "normal";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE:
                protLevel = "signature";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                protLevel = "signatureOrSystem";
                break;
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
            protLevel += "|privileged";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
            protLevel += "|development";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_APPOP) != 0) {
            protLevel += "|appop";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_PRE23) != 0) {
            protLevel += "|pre23";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_INSTALLER) != 0) {
            protLevel += "|installer";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_VERIFIER) != 0) {
            protLevel += "|verifier";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_PREINSTALLED) != 0) {
            protLevel += "|preinstalled";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_SETUP) != 0) {
            protLevel += "|setup";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_INSTANT) != 0) {
            protLevel += "|instant";
        }
        if ((level & PermissionInfo.PROTECTION_FLAG_RUNTIME_ONLY) != 0) {
            protLevel += "|runtime";
        }
        return protLevel;
    }
}