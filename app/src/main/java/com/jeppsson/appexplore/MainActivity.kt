package com.jeppsson.appexplore

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PackageScannerService.enqueueWork(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Enqueue periodic job, as Android O and above will not
            // receive Intent.ACTION_PACKAGE_ADDED
            PeriodicJobService.enqueueWork(this)
        }
    }
}
