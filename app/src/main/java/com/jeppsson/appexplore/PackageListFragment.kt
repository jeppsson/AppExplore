package com.jeppsson.appexplore

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.jeppsson.appexplore.db.Package

class PackageListFragment : Fragment(), Observer<List<Package>>, PackageClickCallback,
    SearchView.OnQueryTextListener {

    private lateinit var adapter: PackageAdapter
    private lateinit var viewModel: PackageListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_package_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PackageAdapter(this)

        (view as RecyclerView).adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(PackageListViewModel::class.java)
        viewModel.packages.observe(this, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.package_list, menu)

        val searchMenuItem = menu.findItem(R.id.menu_action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getString(R.string.search_hint)
        searchView.isIconified = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_app_notification_settings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                    )
                }
                true
            }
            R.id.menu_action_filter -> {
                fragmentManager?.beginTransaction()
                    ?.add(FilterDialogFragment(), "filter_dialog")
                    ?.commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChanged(packages: List<Package>) {
        adapter.submitList(packages)
    }

    override fun onClick(p: String) {
        val uri = Uri.Builder()
            .scheme("package")
            .opaquePart(p)
            .build()
        startActivity(
            Intent(context, AppInfoActivity::class.java)
                .setData(uri)
        )
    }

    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.updateQuery(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }
}
