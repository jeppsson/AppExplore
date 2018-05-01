package com.jeppsson.appexplore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private static final String CHANNEL_ID = "1";

    static void enqueueWork(Context context) {
        enqueueWork(context, PackageScannerService.class, JOB_ID, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        PackageDao dao = PackageDatabase.getAppDatabase(this).dao();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            Package existingPackage = dao.findApp(applicationInfo.packageName);
            if (existingPackage != null) {
                StringBuilder sb = new StringBuilder();
                if (existingPackage.targetSdkVersion != applicationInfo.targetSdkVersion) {
                    sb.append(getString(R.string.notification_update_targetSdkVersion,
                            existingPackage.targetSdkVersion, applicationInfo.targetSdkVersion))
                            .append('\n');
                }

                if (sb.length() > 0) {
                    createNotification(applicationInfo, sb.toString().trim());
                }
            }

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

        for (Package p : dao.allApps()) {
            try {
                getPackageManager().getPackageInfo(p.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                dao.delete(p);
            }
        }
    }

    private void createNotification(ApplicationInfo applicationInfo, String contentText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_apps_white_24dp)
                .setContentTitle(applicationInfo.name + " updated")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(applicationInfo.name, 0, mBuilder.build());
    }
}
