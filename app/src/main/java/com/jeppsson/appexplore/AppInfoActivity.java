package com.jeppsson.appexplore;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jeppsson.appexplore.db.Package;
import com.jeppsson.appexplore.db.PackageDao;
import com.jeppsson.appexplore.db.PackageDatabase;

import java.io.File;

public class AppInfoActivity extends AppCompatActivity implements Observer<Package> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Uri data = getIntent().getData();
        if (data == null) {
            return;
        }

        ViewModelProviders.of(this,
                new PackageViewModel.PackageViewModelFactory(getApplication(), data.getSchemeSpecificPart()))
                .get(PackageViewModel.class)
                .getPackage().observe(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_app_info:
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        getIntent().getData()));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChanged(@Nullable Package p) {
        TextView packageName = findViewById(R.id.value_package_name);
        TextView versionCode = findViewById(R.id.value_version_code);
        TextView versionName = findViewById(R.id.value_version_name);
        TextView dataDir = findViewById(R.id.value_data_directory);
        TextView sourceDir = findViewById(R.id.value_source_directory);
        TextView processName = findViewById(R.id.value_process_name);
        TextView targetSDKVersion = findViewById(R.id.value_target_sdk_version);
        TextView minimumSDKVersion = findViewById(R.id.value_minimum_sdk_version);
        TextView sharedLibraries = findViewById(R.id.value_shared_libraries);
        TextView nativeLibraries = findViewById(R.id.value_native_libraries);
        TextView certificateStart = findViewById(R.id.value_certificate_start);
        TextView certificateEnd = findViewById(R.id.value_certificate_end);
        TextView appFlags = findViewById(R.id.value_app_flags);
        TextView signature = findViewById(R.id.value_signature);
        TextView permissions = findViewById(R.id.value_permissions);
        TextView permissionsNotGranted = findViewById(R.id.value_permissions_not_granted);
        TextView features = findViewById(R.id.value_features);
        TextView featuresNotAvailable = findViewById(R.id.value_features_not_available);
        TextView activities = findViewById(R.id.value_activities);
        TextView services = findViewById(R.id.value_services);
        TextView contentProviders = findViewById(R.id.value_content_providers);
        TextView receivers = findViewById(R.id.value_receivers);

        if (p == null) {
            // Could happen if app has been uninstalled
            finish();
            return;
        }

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(p.packageName,
                    PackageManager.GET_SIGNATURES | PackageManager.GET_PERMISSIONS
                            | PackageManager.GET_CONFIGURATIONS | PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            finish();
            return;
        }

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(p.packageName, PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (PackageManager.NameNotFoundException e) {
            finish();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(p.appName);
        }

        packageName.setText(p.packageName);
        versionCode.setText(getString(R.string.app_info_version_code,
                packageInfo.versionCode, packageInfo.versionCode));
        versionName.setText(packageInfo.versionName);
        dataDir.setText(applicationInfo.dataDir);
        sourceDir.setText(applicationInfo.sourceDir);
        processName.setText(applicationInfo.processName);

        // Libraries
        if (applicationInfo.sharedLibraryFiles != null) {
            sharedLibraries.setText(Utils.arrayToLines(applicationInfo.sharedLibraryFiles, "No shared libraries"));
        }
        if (applicationInfo.nativeLibraryDir != null) {
            nativeLibraries.setText(Utils.arrayToLines(new File(applicationInfo.nativeLibraryDir).list(), applicationInfo.nativeLibraryDir, "No native libraries"));
        }

        // SDK versions
        targetSDKVersion.setText(String.valueOf(applicationInfo.targetSdkVersion));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minimumSDKVersion.setText(String.valueOf(applicationInfo.minSdkVersion));
        }

        // Certificate
        certificateStart.setText(Utils.getCertificateStart(packageInfo.signatures, "No certificate!"));
        certificateEnd.setText(Utils.getCertificateEnd(packageInfo.signatures, "No certificate!"));

        // App flags
        appFlags.setText(getString(R.string.app_info_app_flags, applicationInfo.flags, AppFlagUtils.getReadableFlags(applicationInfo.flags)));

        // Signature
        signature.setText(Utils.getSignature(packageInfo.signatures));

        // Permissions
        permissions.setText(PermissionsUtils.getPermissions(getPackageManager(), packageInfo));
        permissionsNotGranted.setText(PermissionsUtils.getPermissionsNotGranted(packageInfo));

        // Features
        features.setText(FeaturesUtils.getFeatures(packageInfo));
        featuresNotAvailable.setText(FeaturesUtils.getFeaturesNoAvailable(getPackageManager(), packageInfo));

        // App components
        activities.setText(AppComponentUtils.getActivities(packageInfo));
        services.setText(AppComponentUtils.getServices(packageInfo));
        contentProviders.setText(AppComponentUtils.getContentProviders(packageInfo));
        receivers.setText(AppComponentUtils.getReceivers(packageInfo));
    }

    private static class PackageViewModel extends AndroidViewModel {

        private final MediatorLiveData<Package> mPackage = new MediatorLiveData<>();

        PackageViewModel(@NonNull Application application, String p) {
            super(application);

            PackageDao dao = PackageDatabase.getAppDatabase(getApplication()).dao();

            mPackage.addSource(dao.findAppLive(p), mPackage::setValue);
        }

        LiveData<Package> getPackage() {
            return mPackage;
        }

        public static class PackageViewModelFactory extends ViewModelProvider.NewInstanceFactory {

            private final Application mApplication;
            private final String mPackage;

            PackageViewModelFactory(Application application, String p) {
                mApplication = application;
                mPackage = p;
            }

            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PackageViewModel(mApplication, mPackage);
            }
        }
    }

}
