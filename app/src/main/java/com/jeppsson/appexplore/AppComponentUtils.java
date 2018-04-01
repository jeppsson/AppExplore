package com.jeppsson.appexplore;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

final class AppComponentUtils {
    public static String getActivities(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.activities != null) {
            for (ActivityInfo activityInfo : packageInfo.activities) {
                sb.append(activityInfo.name).append('\n');
            }
        } else {
            sb.append("No activities");
        }

        return sb.toString().trim();
    }

    public static String getServices(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.services != null) {
            for (ServiceInfo serviceInfo : packageInfo.services) {
                sb.append(serviceInfo.name).append('\n');
            }
        } else {
            sb.append("No services");
        }

        return sb.toString().trim();
    }

    public static String getContentProviders(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.providers != null) {
            for (ProviderInfo providerInfo : packageInfo.providers) {
                sb.append(providerInfo.name).append('\n');
            }
        } else {
            sb.append("No services");
        }

        return sb.toString().trim();
    }
}
