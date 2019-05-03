package com.jeppsson.appexplore

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.jeppsson.appexplore.databinding.FilterDialogBinding

internal class FilterDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var viewModel: PackageListViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewModel = ViewModelProviders.of(activity!!)
            .get(PackageListViewModel::class.java)

        val binding = DataBindingUtil.inflate<FilterDialogBinding>(
            LayoutInflater.from(context),
            R.layout.filter_dialog,
            null,
            false
        )

        binding.viewmodel = viewModel

        return AlertDialog.Builder(context!!)
            .setTitle(R.string.filter_dialog_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, this)
            .setNeutralButton(R.string.option_clear, this)
            .create()
    }

    override fun onClick(dialogInterface: DialogInterface?, which: Int) {
        when (which) {
            Dialog.BUTTON_NEUTRAL -> {
                viewModel.clearFilter()
            }
        }
    }
}