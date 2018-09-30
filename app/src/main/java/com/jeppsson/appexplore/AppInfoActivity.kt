package com.jeppsson.appexplore

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDatabase
import com.jeppsson.appexplore.utils.*
import java.io.File

class AppInfoActivity : AppCompatActivity(), Observer<Package> {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        val data = intent.data ?: return

        ViewModelProviders.of(this,
                PackageViewModel.PackageViewModelFactory(application, data.schemeSpecificPart))
                .get(PackageViewModel::class.java)
                .packageInfo.observe(this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_app_info -> {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        intent.data))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChanged(@Nullable p: Package?) {
        val packageName = findViewById<TextView>(R.id.value_package_name)
        val versionCode = findViewById<TextView>(R.id.value_version_code)
        val versionName = findViewById<TextView>(R.id.value_version_name)
        val dataDir = findViewById<TextView>(R.id.value_data_directory)
        val sourceDir = findViewById<TextView>(R.id.value_source_directory)
        val processName = findViewById<TextView>(R.id.value_process_name)
        val targetSDKVersion = findViewById<TextView>(R.id.value_target_sdk_version)
        val minimumSDKVersion = findViewById<TextView>(R.id.value_minimum_sdk_version)
        val metaData = findViewById<TextView>(R.id.value_meta_data)
        val sharedLibraries = findViewById<TextView>(R.id.value_shared_libraries)
        val nativeLibraries = findViewById<TextView>(R.id.value_native_libraries)
        val certificateStart = findViewById<TextView>(R.id.value_certificate_start)
        val certificateEnd = findViewById<TextView>(R.id.value_certificate_end)
        val appFlags = findViewById<TextView>(R.id.value_app_flags)
        val signature = findViewById<TextView>(R.id.value_signature)
        val permissions = findViewById<TextView>(R.id.value_permissions)
        val permissionsNotGranted = findViewById<TextView>(R.id.value_permissions_not_granted)
        val features = findViewById<TextView>(R.id.value_features)
        val featuresNotAvailable = findViewById<TextView>(R.id.value_features_not_available)
        val activities = findViewById<TextView>(R.id.value_activities)
        val services = findViewById<TextView>(R.id.value_services)
        val contentProviders = findViewById<TextView>(R.id.value_content_providers)
        val receivers = findViewById<TextView>(R.id.value_receivers)

        if (p == null) {
            // Could happen if app has been uninstalled
            finish()
            return
        }

        val packageInfo: PackageInfo
        try {
            packageInfo = packageManager.getPackageInfo(p.packageName,
                    PackageManager.GET_SIGNATURES or PackageManager.GET_PERMISSIONS
                            or PackageManager.GET_CONFIGURATIONS or PackageManager.GET_ACTIVITIES
                            or PackageManager.GET_SERVICES or PackageManager.GET_PROVIDERS
                            or PackageManager.GET_RECEIVERS)
        } catch (e: PackageManager.NameNotFoundException) {
            finish()
            return
        }

        val applicationInfo: ApplicationInfo
        try {
            applicationInfo = packageManager.getApplicationInfo(p.packageName,
                    PackageManager.GET_SHARED_LIBRARY_FILES or PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            finish()
            return
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = p.appName
        }

        packageName.text = p.packageName
        versionCode.text = getString(R.string.app_info_version_code,
                packageInfo.versionCode, packageInfo.versionCode)
        versionName.text = packageInfo.versionName
        dataDir.text = applicationInfo.dataDir
        sourceDir.text = applicationInfo.sourceDir
        processName.text = applicationInfo.processName

        // Libraries
        if (applicationInfo.sharedLibraryFiles != null) {
            sharedLibraries.text = Utils.arrayToLines(applicationInfo.sharedLibraryFiles)
        }
        if (applicationInfo.nativeLibraryDir != null) {
            nativeLibraries.text = Utils.arrayToLines(File(applicationInfo.nativeLibraryDir).list(),
                    applicationInfo.nativeLibraryDir)
        }

        // SDK versions
        targetSDKVersion.text = applicationInfo.targetSdkVersion.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minimumSDKVersion.text = applicationInfo.minSdkVersion.toString()
        }

        // Meta data
        metaData.text = Utils.metaData(applicationInfo)

        // Certificate
        certificateStart.text = CertUtils.getCertificateStart(packageInfo.signatures)
        certificateEnd.text = CertUtils.getCertificateEnd(packageInfo.signatures)

        // App flags
        appFlags.text = getString(R.string.app_info_app_flags, applicationInfo.flags,
                AppFlagUtils.getReadableFlags(applicationInfo.flags))

        // Signature
        signature.text = CertUtils.getSignature(packageInfo.signatures)

        // Permissions
        permissions.text = PermissionsUtils.getPermissions(packageManager, packageInfo)
        permissionsNotGranted.text = PermissionsUtils.getPermissionsNotGranted(packageInfo)

        // Features
        features.text = FeaturesUtils.getFeatures(packageInfo)
        featuresNotAvailable.text = FeaturesUtils.getFeaturesNoAvailable(packageManager, packageInfo)

        // App components
        activities.text = AppComponentUtils.getActivities(packageInfo)
        services.text = AppComponentUtils.getServices(packageInfo)
        contentProviders.text = AppComponentUtils.getContentProviders(packageInfo)
        receivers.text = AppComponentUtils.getReceivers(packageInfo)
    }

    private class PackageViewModel internal constructor(application: Application, packageName: String) : AndroidViewModel(application) {

        internal val packageInfo: LiveData<Package> =
                PackageDatabase.getAppDatabase(getApplication()).dao().findAppLive(packageName)

        class PackageViewModelFactory internal constructor(private val application: Application, private val packageName: String) : ViewModelProvider.NewInstanceFactory() {

            @NonNull
            override fun <T : ViewModel> create(@NonNull modelClass: Class<T>): T {
                return PackageViewModel(application, packageName) as T
            }
        }
    }

}
