package com.jeppsson.appexplore

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDatabase

class PackageListViewModel(application: Application) : AndroidViewModel(application) {

    private val filterMutable =
        MutableLiveData<PackageFilter>()
    internal val filter: LiveData<PackageFilter> = filterMutable

    internal val packages: LiveData<List<Package>> =
        Transformations.switchMap(filter) { filter ->
            if (filter.targetSdkVersionMin != 1 || filter.targetSdkVersionMax != 29
                || filter.flags != 0
            ) {
                PackageDatabase.getAppDatabase(getApplication())
                    .dao()
                    .findAppsSdkLive(
                        "%${filter.query}%",
                        filter.targetSdkVersionMin,
                        filter.targetSdkVersionMax,
                        filter.flags
                    )
            } else {
                PackageDatabase.getAppDatabase(getApplication())
                    .dao()
                    .findAppsLive("%${filter.query}%")
            }
        }

    fun updateQuery(query: String) {
        val packageFilter = filter.value ?: PackageFilter(
            "",
            1,
            29,
            0
        )
        filterMutable.postValue(
            PackageFilter(
                query,
                packageFilter.targetSdkVersionMin,
                packageFilter.targetSdkVersionMax,
                packageFilter.flags
            )
        )
    }

    fun updateSdkFilter(
        targetSdkVersionMin: Int,
        targetSdkVersionMax: Int
    ) {
        val packageFilter = filter.value ?: PackageFilter(
            "",
            1,
            29,
            0
        )
        filterMutable.postValue(
            PackageFilter(
                packageFilter.query,
                targetSdkVersionMin,
                targetSdkVersionMax,
                packageFilter.flags
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun updateFlagClearText(checked: Boolean) {
        val packageFilter = filter.value ?: PackageFilter(
            "",
            1,
            29,
            0
        )
        val flags = if (checked) {
            packageFilter.flags or ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC
        } else {
            packageFilter.flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC.inv()
        }
        filterMutable.postValue(
            PackageFilter(
                packageFilter.query,
                packageFilter.targetSdkVersionMin,
                packageFilter.targetSdkVersionMax,
                flags
            )
        )
    }

    fun updateFlagDebuggable(checked: Boolean) {
        val packageFilter = filter.value ?: PackageFilter(
            "",
            1,
            29,
            0
        )
        val flags = if (checked) {
            packageFilter.flags or ApplicationInfo.FLAG_DEBUGGABLE
        } else {
            packageFilter.flags and ApplicationInfo.FLAG_DEBUGGABLE.inv()
        }
        filterMutable.postValue(
            PackageFilter(
                packageFilter.query,
                packageFilter.targetSdkVersionMin,
                packageFilter.targetSdkVersionMax,
                flags
            )
        )
    }

    fun clearFilter() {
        filterMutable.postValue(PackageFilter("", 1, 29, 0))
    }

    init {
        filterMutable.value = PackageFilter("", 1, 29, 0)
    }

    internal data class PackageFilter(
        val query: String,
        val targetSdkVersionMin: Int,
        val targetSdkVersionMax: Int,
        val flags: Int
    )
}