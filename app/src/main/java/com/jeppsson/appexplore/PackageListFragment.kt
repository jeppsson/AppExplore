package com.jeppsson.appexplore

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.jeppsson.appexplore.databinding.FragmentPackageListBinding
import com.jeppsson.appexplore.db.Package
import com.jeppsson.appexplore.db.PackageDatabase

class PackageListFragment : Fragment(), Observer<List<Package>>, PackageClickCallback {

    private lateinit var binding: FragmentPackageListBinding
    private lateinit var adapter: PackageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_package_list, container, false)

        return binding.root
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PackageAdapter(this)

        binding.packageList.adapter = adapter
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val model = ViewModelProviders.of(this).get(PackageListViewModel::class.java)
        model.packages.observe(this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.package_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_app_notification_settings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val context = context
                    if (context != null) {
                        startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChanged(@Nullable packages: List<Package>?) {
        if (packages != null) {
            binding.isLoading = false
            adapter.setPackageList(packages)
        } else {
            binding.isLoading = true
        }
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

    class PackageListViewModel(@NonNull application: Application) : AndroidViewModel(application) {

        internal val packages: LiveData<List<Package>>

        init {
            val dao = PackageDatabase.getAppDatabase(getApplication()).dao()
            packages = dao.loadAllApps()
        }
    }
}