package com.jeppsson.appexplore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jeppsson.appexplore.databinding.PackageRowItemBinding
import com.jeppsson.appexplore.db.Package

internal class PackageAdapter(private val packageClickCallback: PackageClickCallback) :
        ListAdapter<Package, PackageAdapter.PackageViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = DataBindingUtil.inflate<PackageRowItemBinding>(LayoutInflater.from(parent.context),
                R.layout.package_row_item, parent, false)
        binding.callback = packageClickCallback
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.binding.pack = getItem(position)
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Package>() {
        override fun areItemsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem.appName == newItem.appName
                    && oldItem.minSdkVersion == newItem.minSdkVersion
                    && oldItem.targetSdkVersion == newItem.targetSdkVersion
        }
    }

    internal class PackageViewHolder(val binding: PackageRowItemBinding) : RecyclerView.ViewHolder(binding.root)
}
