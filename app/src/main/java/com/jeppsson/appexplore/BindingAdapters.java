package com.jeppsson.appexplore;

import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("applicationIcon")
    public static void setApplicationIcon(ImageView view, String packageName) {
        PackageManager pm = view.getContext().getPackageManager();
        Drawable icon;
        try {
            icon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        view.setImageDrawable(icon);
    }
}