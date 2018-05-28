package com.jeppsson.appexplore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Enqueue periodic job, as Android O and above will not
            // receive Intent.ACTION_PACKAGE_ADDED
            PeriodicJobService.enqueueWork(this);

            // Create notification channel
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_channel_description));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
