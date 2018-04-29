package com.jeppsson.appexplore;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeppsson.appexplore.databinding.FragmentPackageListBinding;
import com.jeppsson.appexplore.db.Package;
import com.jeppsson.appexplore.db.PackageDao;
import com.jeppsson.appexplore.db.PackageDatabase;

import java.util.List;

public class PackageListFragment extends Fragment
        implements Observer<List<Package>>, PackageClickCallback {

    private FragmentPackageListBinding mBinding;
    private PackageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_package_list, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PackageAdapter(this);

        mBinding.packageList.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PackageListViewModel model =
                ViewModelProviders.of(this).get(PackageListViewModel.class);
        model.getPackages().observe(this, this);
    }

    @Override
    public void onChanged(@Nullable List<Package> packages) {
        if (packages != null) {
            mBinding.setIsLoading(false);
            mAdapter.setPackageList(packages);
        } else {
            mBinding.setIsLoading(true);
        }
    }

    @Override
    public void onClick(String p) {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Uri uri = new Uri.Builder()
                    .scheme("package")
                    .opaquePart(p)
                    .build();
            startActivity(new Intent(getContext(), AppInfoActivity.class)
                    .setData(uri));
        }
    }

    public static class PackageListViewModel extends AndroidViewModel {

        private final LiveData<List<Package>> mPackages;

        public PackageListViewModel(@NonNull Application application) {
            super(application);

            PackageDao dao = PackageDatabase.getAppDatabase(getApplication()).dao();
            mPackages = dao.loadAllApps();
        }

        LiveData<List<Package>> getPackages() {
            return mPackages;
        }
    }
}
