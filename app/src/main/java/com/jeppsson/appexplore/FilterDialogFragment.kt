package com.jeppsson.appexplore

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

internal class FilterDialogFragment : DialogFragment(), DialogInterface.OnClickListener,
    Observer<PackageListViewModel.PackageFilter> {

    private lateinit var editTargetSdkVersionMin: EditText
    private lateinit var editTargetSdkVersionMax: EditText
    private lateinit var checkFlagDebuggable: CheckBox
    private lateinit var checkFlagClearText: CheckBox
    private lateinit var viewModel: PackageListViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProviders.of(activity!!)
            .get(PackageListViewModel::class.java)
        viewModel.filter.observe(this, this)

        return AlertDialog.Builder(context!!)
            .setTitle(R.string.filter_dialog_title)
            .setView(R.layout.filter_dialog)
            .setPositiveButton(android.R.string.ok, this)
            .setNeutralButton(R.string.option_clear, this)
            .create()
    }

    override fun onStart() {
        super.onStart()

        editTargetSdkVersionMin = dialog.findViewById(R.id.target_sdk_version_min)
        editTargetSdkVersionMin.addTextChangedListener(FilterTextWatcher { onUpdate() })
        editTargetSdkVersionMax = dialog.findViewById(R.id.target_sdk_version_max)
        editTargetSdkVersionMax.addTextChangedListener(FilterTextWatcher { onUpdate() })
        checkFlagDebuggable = dialog.findViewById(R.id.flag_debuggable)
        checkFlagDebuggable.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckBoxUpdated(buttonView, isChecked)
        }
        checkFlagClearText = dialog.findViewById(R.id.flag_uses_clear_text_traffic)
        checkFlagClearText.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckBoxUpdated(buttonView, isChecked)
        }
        if (Build.VERSION.SDK_INT < 23) {
            checkFlagClearText.visibility = View.GONE
        }
    }

    @SuppressLint("NewApi")
    private fun onCheckBoxUpdated(buttonView: CompoundButton?, checked: Boolean) {
        when (buttonView) {
            checkFlagClearText -> viewModel.updateFlagClearText(checked)
            checkFlagDebuggable -> viewModel.updateFlagDebuggable(checked)
        }
    }

    override fun onChanged(filter: PackageListViewModel.PackageFilter) {
        if (editTargetSdkVersionMin.text.toString() != filter.targetSdkVersionMin.toString() &&
            editTargetSdkVersionMin.text.isNotEmpty()
        ) {
            editTargetSdkVersionMin.setText(filter.targetSdkVersionMin.toString())
        }
        if (editTargetSdkVersionMax.text.toString() != filter.targetSdkVersionMax.toString() &&
            editTargetSdkVersionMax.text.isNotEmpty()
        ) {
            editTargetSdkVersionMax.setText(filter.targetSdkVersionMax.toString())
        }
        if (checkFlagDebuggable.isChecked != (filter.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)) {
            checkFlagDebuggable.isChecked = filter.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }
        if (Build.VERSION.SDK_INT >= 23 &&
            (checkFlagClearText.isChecked != (filter.flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0))
        ) {
            checkFlagClearText.isChecked =
                filter.flags and ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0
        }
    }

    override fun onClick(dialogInterface: DialogInterface?, which: Int) {
        when (which) {
            Dialog.BUTTON_NEUTRAL -> {
                viewModel.clearFilter()
            }
        }
    }

    private fun onUpdate() {
        val targetSdkVersionMin = editTargetSdkVersionMin.text.toString().toIntOrNull()
            ?: getString(R.string.filter_sdk_version_min).toInt()
        val targetSdkVersionMax = editTargetSdkVersionMax.text.toString().toIntOrNull()
            ?: getString(R.string.filter_sdk_version_max).toInt()

        viewModel.updateSdkFilter(
            targetSdkVersionMin,
            targetSdkVersionMax
        )
    }

    private class FilterTextWatcher(val onChanged: () -> Unit) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            onChanged()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}