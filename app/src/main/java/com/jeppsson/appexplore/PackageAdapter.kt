package com.jeppsson.appexplore

import androidx.databinding.DataBindingUtil
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.jeppsson.appexplore.databinding.PackageRowItemBinding
import com.jeppsson.appexplore.db.Package

internal class PackageAdapter(@param:Nullable @field:Nullable
                              private val mPackageClickCallback: PackageClickCallback) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    private var packageList: List<Package> = ArrayList()

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = DataBindingUtil.inflate<PackageRowItemBinding>(LayoutInflater.from(parent.context),
                R.layout.package_row_item, parent, false)
        binding.callback = mPackageClickCallback
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: PackageViewHolder, position: Int) {
        holder.binding.pack = packageList[position]
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    fun setPackageList(apps: List<Package>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return packageList.size
            }

            override fun getNewListSize(): Int {
                return apps.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return packageList[oldItemPosition].packageName == apps[newItemPosition].packageName
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val newVerb = apps[newItemPosition]
                val oldVerb = packageList[oldItemPosition]
                return newVerb.packageName == oldVerb.packageName
            }
        })
        packageList = apps
        result.dispatchUpdatesTo(this)

    }

    internal class PackageViewHolder(val binding: PackageRowItemBinding) : RecyclerView.ViewHolder(binding.root)
}
