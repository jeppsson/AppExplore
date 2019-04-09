package com.jeppsson.appexplore

import android.app.Application
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
            if (filter.targetSdkVersionMin != 1 || filter.targetSdkVersionMax != 29) {
                PackageDatabase.getAppDatabase(getApplication())
                    .dao()
                    .findAppsSdkLive(
                        "%${filter.query}%",
                        filter.targetSdkVersionMin,
                        filter.targetSdkVersionMax
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
            29
        )
        filterMutable.postValue(
            PackageFilter(
                query,
                packageFilter.targetSdkVersionMin,
                packageFilter.targetSdkVersionMax
            )
        )
    }

    fun updateSdkFilter(
        targetSdkVersionMin: Int,
        targetSdkVersionMax: Int
    ) {
        val query = filter.value?.query ?: ""
        filterMutable.postValue(
            PackageFilter(
                query,
                targetSdkVersionMin,
                targetSdkVersionMax
            )
        )
    }

    init {
        filterMutable.value = PackageFilter("", 1, 29)
    }

    internal data class PackageFilter(
        val query: String,
        val targetSdkVersionMin: Int,
        val targetSdkVersionMax: Int
    )
}