package com.jeppsson.appexplore;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jeppsson.appexplore.db.Package;
import com.jeppsson.appexplore.db.PackageDao;
import com.jeppsson.appexplore.db.PackageDatabase;

import java.util.List;

public class PackageScannerService extends JobIntentService {

    private static final int JOB_ID = 1000;

    static void enqueueWork(Context context) {
        enqueueWork(context, PackageScannerService.class, JOB_ID, new Intent());
    }

    static void enqueueWork(Context context, @NonNull Intent intent) {
        enqueueWork(context, PackageScannerService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();

        PackageDao dao = PackageDatabase.getAppDatabase(this).dao();
        PackageManager pm = getPackageManager();

        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            Uri data = intent.getData();
            if (data != null) {
                ApplicationInfo applicationInfo;
                try {
                    applicationInfo = pm.getApplicationInfo(
                            data.getSchemeSpecificPart(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    return;
                }

                notifyUpdate(dao, pm, applicationInfo);
                updatePackage(dao, pm, applicationInfo);
            }
        } else if (Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
            Uri data = intent.getData();
            if (data != null) {
                Package p = new Package();
                p.packageName = data.getSchemeSpecificPart();
                dao.delete(p);
            }
        } else {
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo applicationInfo : packages) {
                notifyUpdate(dao, pm, applicationInfo);
                updatePackage(dao, pm, applicationInfo);
            }

            for (Package p : dao.allApps()) {
                try {
                    getPackageManager().getPackageInfo(p.packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    dao.delete(p);
                }
            }
        }
    }

    private void notifyUpdate(PackageDao dao, PackageManager pm, ApplicationInfo applicationInfo) {
        Package existingPackage = dao.findApp(applicationInfo.packageName);

        if (existingPackage != null) {
            StringBuilder sb = new StringBuilder();
            if (existingPackage.targetSdkVersion != applicationInfo.targetSdkVersion) {
                sb.append(getString(R.string.notification_update_targetSdkVersion,
                        existingPackage.targetSdkVersion, applicationInfo.targetSdkVersion));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (existingPackage.minSdkVersion != applicationInfo.minSdkVersion) {
                    sb.append(getString(R.string.notification_update_minimumSDKVersion,
                            existingPackage.minSdkVersion, applicationInfo.minSdkVersion));
                }
            }

            if (sb.length() > 0) {
                createNotification(pm, applicationInfo, sb.toString().trim());
            }
        }
    }

    private void updatePackage(PackageDao dao, PackageManager pm, ApplicationInfo applicationInfo) {
        Package p = new Package();
        p.appName = pm.getApplicationLabel(applicationInfo).toString();
        p.packageName = applicationInfo.packageName;
        p.targetSdkVersion = applicationInfo.targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            p.minSdkVersion = applicationInfo.minSdkVersion;
        }

        int updates = dao.update(p);
        if (updates == 0) {
            dao.insert(p);
        }
    }

    private void createNotification(PackageManager pm, ApplicationInfo applicationInfo, String contentText) {

        Uri uri = new Uri.Builder()
                .scheme("package")
                .opaquePart(applicationInfo.packageName)
                .build();
        Intent appInfoIntent = new Intent(this, AppInfoActivity.class)
                .setData(uri);
        PendingIntent pendingIntent =
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(appInfoIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_apps_white_24dp)
                        .setContentTitle(getString(R.string.notification_title,
                                pm.getApplicationLabel(applicationInfo)))
                        .setContentText(contentText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(applicationInfo.name, 0, mBuilder.build());
    }
}
