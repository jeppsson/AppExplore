package com.jeppsson.appexplore

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jeppsson.appexplore.databinding.ActivityAppInfoBinding
import com.jeppsson.appexplore.utils.*

class AppInfoActivity : AppCompatActivity() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAppInfoBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_app_info)

        val data = intent.data ?: return

        val appFlags = findViewById<TextView>(R.id.value_app_flags)

        val packageInfo: PackageInfo
        try {
            packageInfo = packageManager.getPackageInfo(data.schemeSpecificPart,
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
            applicationInfo = packageManager.getApplicationInfo(data.schemeSpecificPart,
                    PackageManager.GET_SHARED_LIBRARY_FILES or PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            finish()
            return
        }

        supportActionBar?.title = packageManager.getApplicationLabel(applicationInfo)

        binding.packageInfo = packageInfo
        binding.applicationInfo = applicationInfo

        // App flags
        appFlags.text = getString(R.string.app_info_app_flags, applicationInfo.flags,
                AppFlagUtils.getReadableFlags(applicationInfo.flags))
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
}
