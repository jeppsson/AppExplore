package com.jeppsson.appexplore;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageScannerService.enqueueWork(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Enqueue periodic job, as Android O and above will not
            // receive Intent.ACTION_PACKAGE_ADDED
            PeriodicJobService.enqueueWork(this);
        }
    }
}
