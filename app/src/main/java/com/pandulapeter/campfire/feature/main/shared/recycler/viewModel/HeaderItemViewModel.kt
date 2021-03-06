package com.pandulapeter.campfire.feature.main.shared.recycler.viewModel

import android.view.View

data class HeaderItemViewModel(val title: Any, val refreshAction: (() -> Unit)? = null) : ItemViewModel {

    override fun getItemId() = title.hashCode().toLong()
    val shouldShowRefreshButton = refreshAction != null

    fun onRefreshButtonPressed(view: View) {
        refreshAction?.invoke()
        view.animate().cancel()
        view.animate().rotationBy(360f).start()
    }
}