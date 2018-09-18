package com.jeppsson.appexplore;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jeppsson.appexplore.databinding.PackageRowItemBinding;
import com.jeppsson.appexplore.db.Package;

import java.util.List;

class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    @Nullable
    private final PackageClickCallback mPackageClickCallback;

    private List<Package> mPackageList;

    PackageAdapter(@Nullable PackageClickCallback packageClickCallback) {
        mPackageClickCallback = packageClickCallback;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PackageRowItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.package_row_item, parent, false);
        binding.setCallback(mPackageClickCallback);
        return new PackageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        holder.mBinding.setPack(mPackageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPackageList == null ? 0 : mPackageList.size();
    }

    public void setPackageList(List<Package> apps) {
        if (mPackageList == null) {
            mPackageList = apps;
            notifyItemRangeInserted(0, mPackageList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mPackageList.size();
                }

                @Override
                public int getNewListSize() {
                    return apps.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mPackageList.get(oldItemPosition).packageName.equals(
                            apps.get(newItemPosition).packageName);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Package newVerb = apps.get(newItemPosition);
                    Package oldVerb = mPackageList.get(oldItemPosition);
                    return newVerb.packageName.equals(oldVerb.packageName);
                }
            });
            mPackageList = apps;
            result.dispatchUpdatesTo(this);

        }
    }

    static class PackageViewHolder extends RecyclerView.ViewHolder {

        final PackageRowItemBinding mBinding;

        PackageViewHolder(PackageRowItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
