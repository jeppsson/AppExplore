package com.jeppsson.appexplore;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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

public class PackageListFragment extends Fragment {

    private FragmentPackageListBinding mBinding;
    private PackageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_package_list, container, false);

        mAdapter = new PackageAdapter(mPackageClickCallback);

        mBinding.packageList.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PackageListViewModel model = ViewModelProviders.of(this).get(PackageListViewModel.class);
        model.getApps().observe(this, apps -> {
            if (apps != null) {
                mBinding.setIsLoading(false);
                mAdapter.setPackageList(apps);
            } else {
                mBinding.setIsLoading(true);
            }
        });
    }

    private final PackageClickCallback mPackageClickCallback = new PackageClickCallback() {
        @Override
        public void onClick(String p) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                Intent intent = new Intent(getContext(), AppInfoActivity.class);
                intent.putExtra(AppInfoActivity.PACKAGE_NAME, p);
                startActivity(intent);
            }
        }
    };

    public static class PackageListViewModel extends AndroidViewModel {

        private final LiveData<List<Package>> mPackages;

        public PackageListViewModel(@NonNull Application application) {
            super(application);

            PackageDao dao = PackageDatabase.getAppDatabase(getApplication()).dao();
            mPackages = dao.loadAllApps();
        }

        LiveData<List<Package>> getApps() {
            return mPackages;
        }
    }
}
