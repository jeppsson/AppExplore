package com.jeppsson.appexplore;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

final class AppComponentUtils {

    private static final int LAUNCH_MULTIPLE = 0;
    private static final int LAUNCH_SINGLE_TOP = 1;
    private static final int LAUNCH_SINGLE_TASK = 2;
    private static final int LAUNCH_SINGLE_INSTANCE = 3;

    public static String getActivities(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.activities != null) {
            for (ActivityInfo activityInfo : packageInfo.activities) {
                sb.append(activityInfo.name).append('\n');
                if (activityInfo.exported) {
                    sb.append("Exported: true").append('\n');
                }
                if (activityInfo.launchMode != 0) {
                    sb.append("LaunchMode: ")
                            .append(launchMode(activityInfo.launchMode))
                            .append('\n');
                }
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
                sb.append(serviceInfo.name).append('\n')
                        .append("Exported: ").append(serviceInfo.exported).append('\n');
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
                sb.append(providerInfo.name).append('\n')
                        .append("Exported: ").append(providerInfo.exported).append('\n');
            }
        } else {
            sb.append("No content providers");
        }

        return sb.toString().trim();
    }

    public static String getReceivers(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.receivers != null) {
            for (ActivityInfo activityInfo : packageInfo.receivers) {
                sb.append(activityInfo.name).append('\n')
                        .append("Exported: ").append(activityInfo.exported).append('\n');
            }
        } else {
            sb.append("No receivers");
        }

        return sb.toString().trim();
    }


    private static String launchMode(int launchMode) {
        switch (launchMode) {
            case LAUNCH_MULTIPLE:
                return "standard";

            case LAUNCH_SINGLE_TOP:
                return "singleTop";

            case LAUNCH_SINGLE_TASK:
                return "singleTask";

            case LAUNCH_SINGLE_INSTANCE:
                return "singleInstance";

            default:
                return "unknown";
        }
    }
}
