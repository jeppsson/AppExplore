package com.jeppsson.appexplore

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.lifecycle.*
import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDatabase

class PackageListViewModel(application: Application) : AndroidViewModel(application) {

    private val defaultTargetSdkVersionMin: Int =
        application.getString(R.string.filter_sdk_version_min).toInt()
    private val defaultTargetSdkVersionMax: Int =
        application.getString(R.string.filter_sdk_version_max).toInt()

    private val filter: MediatorLiveData<PackageFilter> = MediatorLiveData()

    var searchQuery: MutableLiveData<String> = MutableLiveData()
    var targetSdkVersionMin: MutableLiveData<String> = MutableLiveData()
    var targetSdkVersionMax: MutableLiveData<String> = MutableLiveData()
    var debuggable: MutableLiveData<Boolean> = MutableLiveData()
    var usesClearTextTraffic: MutableLiveData<Boolean> = MutableLiveData()

    init {
        searchQuery.value = ""
        targetSdkVersionMin.value = defaultTargetSdkVersionMin.toString()
        targetSdkVersionMax.value = defaultTargetSdkVersionMax.toString()
        debuggable.value = false
        usesClearTextTraffic.value = false

        filter.addSource(searchQuery) { updateFilter() }
        filter.addSource(targetSdkVersionMin) { updateFilter() }
        filter.addSource(targetSdkVersionMax) { updateFilter() }
        filter.addSource(debuggable) { updateFilter() }
        filter.addSource(usesClearTextTraffic) { updateFilter() }
    }

    internal val packages: LiveData<List<Package>> =
        Transformations.switchMap(filter) { filter ->
            if (filter.targetSdkVersionMin != defaultTargetSdkVersionMin ||
                filter.targetSdkVersionMax != defaultTargetSdkVersionMax ||
                filter.flags != 0
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
        searchQuery.postValue(query)
    }

    fun clearFilter() {
        targetSdkVersionMin.value = defaultTargetSdkVersionMin.toString()
        targetSdkVersionMax.value = defaultTargetSdkVersionMax.toString()
        debuggable.value = false
        usesClearTextTraffic.value = false
    }

    private fun updateFilter() {
        var flags = if (debuggable.value == true) {
            ApplicationInfo.FLAG_DEBUGGABLE
        } else {
            0
        }
        flags = if (usesClearTextTraffic.value == true && Build.VERSION.SDK_INT >= 23) {
            flags or ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC
        } else {
            flags
        }

        filter.value = PackageFilter(
            searchQuery.value ?: "",
            targetSdkVersionMin.value?.toIntOrNull() ?: defaultTargetSdkVersionMin,
            targetSdkVersionMax.value?.toIntOrNull() ?: defaultTargetSdkVersionMax,
            flags
        )
    }

    private data class PackageFilter(
        val query: String,
        val targetSdkVersionMin: Int,
        val targetSdkVersionMax: Int,
        val flags: Int
    )
}