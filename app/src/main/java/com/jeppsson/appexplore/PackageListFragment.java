package com.jeppsson.appexplore;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.package_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_app_notification_settings:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Context context = getContext();
                    if (context != null) {
                        startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName()));
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
