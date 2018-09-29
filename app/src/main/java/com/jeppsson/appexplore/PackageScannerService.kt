package com.jeppsson.appexplore

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder

import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDao
import com.jeppsson.appexplore.db.PackageDatabase

class PackageScannerService : JobIntentService() {

    override fun onHandleWork(@NonNull intent: Intent) {
        val action = intent.action

        val dao = PackageDatabase.getAppDatabase(this).dao()
        val pm = packageManager

        if (Intent.ACTION_PACKAGE_ADDED == action) {
            val data = intent.data
            if (data != null) {
                val applicationInfo: ApplicationInfo
                try {
                    applicationInfo = pm.getApplicationInfo(
                            data.schemeSpecificPart, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    return
                }

                notifyUpdate(dao, pm, applicationInfo)
                updatePackage(dao, pm, applicationInfo)
            }
        } else if (Intent.ACTION_PACKAGE_FULLY_REMOVED == action) {
            val data = intent.data
            if (data != null) {
                val p = Package()
                p.packageName = data.schemeSpecificPart
                dao.delete(p)
            }
        } else {
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            for (applicationInfo in packages) {
                notifyUpdate(dao, pm, applicationInfo)
                updatePackage(dao, pm, applicationInfo)
            }

            // Remove uninstalled applications from data base
            for (p in dao.allApps()) {
                try {
                    packageManager.getPackageInfo(p.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    dao.delete(p)
                }
            }
        }
    }

    private fun notifyUpdate(dao: PackageDao, pm: PackageManager, applicationInfo: ApplicationInfo) {
        val existingPackage = dao.findApp(applicationInfo.packageName)

        if (existingPackage != null) {
            val sb = StringBuilder()
            if (existingPackage.targetSdkVersion != applicationInfo.targetSdkVersion) {
                sb.append(getString(R.string.notification_update_targetSdkVersion,
                        existingPackage.targetSdkVersion, applicationInfo.targetSdkVersion))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (existingPackage.minSdkVersion != applicationInfo.minSdkVersion) {
                    sb.append(getString(R.string.notification_update_minimumSDKVersion,
                            existingPackage.minSdkVersion, applicationInfo.minSdkVersion))
                }
            }

            if (sb.isNotEmpty()) {
                createNotification(pm, applicationInfo, sb.toString().trim())
            }
        }
    }

    private fun updatePackage(dao: PackageDao, pm: PackageManager, applicationInfo: ApplicationInfo) {
        val p = Package()
        p.appName = pm.getApplicationLabel(applicationInfo).toString()
        p.packageName = applicationInfo.packageName
        p.targetSdkVersion = applicationInfo.targetSdkVersion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            p.minSdkVersion = applicationInfo.minSdkVersion
        }

        val updates = dao.update(p)
        if (updates == 0) {
            dao.insert(p)
        }
    }

    private fun createNotification(pm: PackageManager, applicationInfo: ApplicationInfo, contentText: String) {
        val uri = Uri.Builder()
                .scheme("package")
                .opaquePart(applicationInfo.packageName)
                .build()
        val appInfoIntent = Intent(this, AppInfoActivity::class.java)
                .setData(uri)
        val pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(appInfoIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder =
                NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_apps_white_24dp)
                        .setContentTitle(getString(R.string.notification_title,
                                pm.getApplicationLabel(applicationInfo)))
                        .setContentText(contentText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(applicationInfo.name, 0, notificationBuilder.build())
    }

    companion object {

        private const val JOB_ID = 1001

        internal fun enqueueWork(context: Context) {
            JobIntentService.enqueueWork(context, PackageScannerService::class.java, JOB_ID, Intent())
        }

        internal fun enqueueWork(context: Context, @NonNull intent: Intent) {
            JobIntentService.enqueueWork(context, PackageScannerService::class.java, JOB_ID, intent)
        }
    }
}
