package com.pandulapeter.campfire.feature.shared.dialog

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.pandulapeter.campfire.util.BundleArgumentDelegate
import com.pandulapeter.campfire.util.withArguments

class AlertDialogFragment : BaseDialogFragment() {

    companion object {
        private var Bundle?.id by BundleArgumentDelegate.Int("id")
        private var Bundle?.title by BundleArgumentDelegate.Int("title")
        private var Bundle?.message by BundleArgumentDelegate.Int("message")
        private var Bundle?.positiveButton by BundleArgumentDelegate.Int("positiveButton")
        private var Bundle?.negativeButton by BundleArgumentDelegate.Int("negativeButton")
        private var Bundle?.cancellable by BundleArgumentDelegate.Boolean("cancellable")

        fun show(
            id: Int,
            fragmentManager: androidx.fragment.app.FragmentManager,
            @StringRes title: Int,
            @StringRes message: Int,
            @StringRes positiveButton: Int,
            @StringRes negativeButton: Int,
            cancellable: Boolean = true
        ) = AlertDialogFragment().withArguments {
            it.id = id
            it.title = title
            it.message = message
            it.positiveButton = positiveButton
            it.negativeButton = negativeButton
            it.cancellable = cancellable
        }.run {
            show(fragmentManager, tag)
        }
    }

    override fun AlertDialog.Builder.createDialog(arguments: Bundle?): AlertDialog = setTitle(arguments.title)
        .setMessage(arguments.message)
        .setPositiveButton(arguments.positiveButton) { _, _ -> onDialogItemSelectedListener?.onPositiveButtonSelected(arguments.id) }
        .setNegativeButton(arguments.negativeButton) { _, _ -> onDialogItemSelectedListener?.onNegativeButtonSelected(arguments.id) }
        .create()

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).also {
        isCancelable = arguments?.cancellable == true
    }
}