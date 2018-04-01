package com.jeppsson.appexplore;

import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

class FeaturesUtils {
    static String getFeatures(PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.reqFeatures != null) {
            for (FeatureInfo featureInfo : packageInfo.reqFeatures) {
                if (featureInfo.name == null) {
                    sb.append(reqGlEsVersionToString(featureInfo));
                } else {
                    sb.append(featureInfo.name)
                            .append(" (")
                            .append(featureFlagsToString(featureInfo));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        sb.append(featureVersionToString(featureInfo));
                    }
                    sb.append(')');
                }
                sb.append('\n');
            }
        }

        return sb.toString().trim();
    }

    private static String featureFlagsToString(FeatureInfo featureInfo) {
        return "required="
                + ((featureInfo.flags & FeatureInfo.FLAG_REQUIRED) == 0 ? "false" : true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static String featureVersionToString(FeatureInfo featureInfo) {
        return featureInfo.version > 0 ? ", version=" + featureInfo.version : "";
    }

    private static String reqGlEsVersionToString(FeatureInfo featureInfo) {
        return featureInfo.name == null ? "reqGlEsVersion=" + featureInfo.getGlEsVersion() : "";
    }

    public static String getFeaturesNoAvailable(PackageManager packageManager, PackageInfo packageInfo) {
        StringBuilder sb = new StringBuilder();

        if (packageInfo.reqFeatures != null) {
            for (FeatureInfo featureInfo : packageInfo.reqFeatures) {
                if (featureInfo.name != null) {
                    if (!packageManager.hasSystemFeature(featureInfo.name)) {
                        sb.append(featureInfo.name)
                                .append(" (")
                                .append(featureFlagsToString(featureInfo));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            sb.append(featureVersionToString(featureInfo));
                        }
                        sb.append(')');
                    }
                }
                sb.append('\n');
            }
        }

        return sb.toString().trim();
    }
}
