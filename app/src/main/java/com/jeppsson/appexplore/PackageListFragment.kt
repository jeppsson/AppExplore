package com.jeppsson.appexplore

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDatabase

class PackageListFragment : Fragment(), Observer<List<Package>>, PackageClickCallback {

    private lateinit var adapter: PackageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_package_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PackageAdapter(this)

        view.findViewById<RecyclerView>(R.id.package_list).adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProviders.of(this).get(PackageListViewModel::class.java)
                .packages.observe(this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.package_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_app_notification_settings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChanged(packages: List<Package>) {
        adapter.setPackageList(packages)
    }

    override fun onClick(p: String) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val uri = Uri.Builder()
                    .scheme("package")
                    .opaquePart(p)
                    .build()
            startActivity(Intent(context, AppInfoActivity::class.java)
                    .setData(uri))
        }
    }

    class PackageListViewModel(application: Application) : AndroidViewModel(application) {

        internal val packages: LiveData<List<Package>> =
                PackageDatabase.getAppDatabase(getApplication()).dao().loadAllApps()
    }
}
