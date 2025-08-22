package com.example.running_app.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.running_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelRunTrackingDialog: DialogFragment() {

    private var dialogListener: (() -> Unit) ?= null

    fun setDialogListener(listener: () -> Unit){
        dialogListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Do you want to cancel the run?")
            .setMessage("Are you sure you want to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->

                dialogListener?.let { listener ->
                    listener()
                }
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }

}