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

    static final String PACKAGE_NAME = "jeppsson.extra.PACKAGE_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        String extraPackageName = getIntent().getStringExtra(PACKAGE_NAME);

        PackageViewModel model = ViewModelProviders.of(this, new PackageViewModel.PackageViewModelFactory(getApplication(), extraPackageName)).get(PackageViewModel.class);
        model.getPackage().observe(this, this);
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
                Uri uri = new Uri.Builder()
                        .scheme("package")
                        .opaquePart(getIntent().getStringExtra(PACKAGE_NAME))
                        .build();
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChanged(@Nullable Package p) {
        TextView packageName = findViewById(R.id.app_info_package_name_value);
        TextView versionCode = findViewById(R.id.app_info_version_code_value);
        TextView versionName = findViewById(R.id.app_info_version_name_value);
        TextView dataDir = findViewById(R.id.app_info_data_directory_value);
        TextView sourceDir = findViewById(R.id.app_info_source_directory_value);
        TextView processName = findViewById(R.id.app_info_process_name_value);
        TextView targetSDKVersion = findViewById(R.id.app_info_target_sdk_version_value);
        TextView minimumSDKVersion = findViewById(R.id.app_info_minimum_sdk_version_value);
        TextView sharedLibraries = findViewById(R.id.app_info_shared_libraries_value);
        TextView nativeLibraries = findViewById(R.id.app_info_native_libraries_value);
        TextView certificateStart = findViewById(R.id.app_info_certificate_start_value);
        TextView certificateEnd = findViewById(R.id.app_info_certificate_end_value);
        TextView appFlags = findViewById(R.id.app_info_app_flags_value);
        TextView signature = findViewById(R.id.app_info_signature_value);
        TextView permissions = findViewById(R.id.app_info_permissions_value);
        TextView permissionsNotGranted = findViewById(R.id.app_info_permissions_not_granted_value);
        TextView features = findViewById(R.id.app_info_features_value);
        TextView featuresNotAvailable = findViewById(R.id.app_info_features_not_available_value);
        TextView activities = findViewById(R.id.app_info_activities_value);
        TextView services = findViewById(R.id.app_info_services_value);
        TextView contentProviders = findViewById(R.id.app_info_content_providers_value);

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(p.packageName,
                    PackageManager.GET_SIGNATURES | PackageManager.GET_PERMISSIONS
                            | PackageManager.GET_CONFIGURATIONS | PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(p.packageName, PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (PackageManager.NameNotFoundException e) {
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
        sharedLibraries.setText(Utils.arrayToLines(applicationInfo.sharedLibraryFiles, "No shared libraries"));
        nativeLibraries.setText(Utils.arrayToLines(new File(applicationInfo.nativeLibraryDir).list(), applicationInfo.nativeLibraryDir, "No native libraries"));

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
        signature.setText(Utils.getSignature(packageInfo.signatures, "default"));

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
