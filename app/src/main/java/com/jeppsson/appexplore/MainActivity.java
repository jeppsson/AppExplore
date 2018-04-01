package com.jeppsson.appexplore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT = "PACKAGE_LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new PackageListFragment(), TAG_FRAGMENT).commit();
        }

        PackageScannerService.enqueueWork(this);
    }
}
