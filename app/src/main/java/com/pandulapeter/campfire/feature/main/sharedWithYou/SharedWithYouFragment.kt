package com.pandulapeter.campfire.feature.main.sharedWithYou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.feature.main.shared.baseSongList.BaseSongListFragment
import com.pandulapeter.campfire.feature.shared.widget.StateLayout
import com.pandulapeter.campfire.feature.shared.widget.ToolbarButton
import com.pandulapeter.campfire.integration.AnalyticsManager
import com.pandulapeter.campfire.integration.fromDeepLinkUri
import com.pandulapeter.campfire.util.BundleArgumentDelegate
import com.pandulapeter.campfire.util.visibleOrGone
import com.pandulapeter.campfire.util.withArguments
import org.koin.androidx.viewmodel.ext.android.viewModel

class SharedWithYouFragment : BaseSongListFragment<SharedWithYouViewModel>() {

    override val shouldSendMultipleSongs = true
    override val viewModel by viewModel<SharedWithYouViewModel>()
    private val shareButton: ToolbarButton by lazy {
        getCampfireActivity()!!.toolbarContext.createToolbarButton(R.drawable.ic_share) {
            viewModel.songIds.let { songIds ->
                analyticsManager.onShareButtonPressed(AnalyticsManager.PARAM_VALUE_SCREEN_SHARED_WITH_YOU, songIds.size)
                shareSongs(songIds)
            }
        }.apply { visibleOrGone = false }
    }
    private val shuffleButton: ToolbarButton by lazy {
        getCampfireActivity()!!.toolbarContext.createToolbarButton(R.drawable.ic_shuffle) {
            shuffleSongs(AnalyticsManager.PARAM_VALUE_SCREEN_SHARED_WITH_YOU)
        }.apply { visibleOrGone = false }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsManager.onTopLevelScreenOpened(AnalyticsManager.PARAM_VALUE_SCREEN_MANAGE_DOWNLOADS)
        binding.swipeRefreshLayout.isEnabled = false
        updateToolbarTitle(viewModel.songCount.value ?: 0)
        getCampfireActivity()?.updateToolbarButtons(listOf(shuffleButton, shareButton))
        viewModel.shouldTryAgain.observeAndReset {
            viewModel.state.value = StateLayout.State.LOADING
            parseLink()
        }
        viewModel.state.observe { updateToolbarTitle(viewModel.songCount.value ?: 0) }
        viewModel.songCount.observe {
            updateToolbarTitle(it)
            shuffleButton.visibleOrGone = it > 1
            shareButton.visibleOrGone = it > 0
        }
        parseLink()
    }

    private fun parseLink() = FirebaseDynamicLinks.getInstance()
        .getDynamicLink(arguments?.intent as Intent)
        .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            //TODO: Find a way to distinguish between parsing errors and invalid links.
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink != null) {
                val songIds = deepLink.toString().fromDeepLinkUri()
                if (songIds.isNotEmpty()) {
                    viewModel.songIds = songIds
                    return@addOnSuccessListener
                }
            }
            viewModel.state.value = StateLayout.State.ERROR
        }
        .addOnFailureListener { viewModel.state.value = StateLayout.State.ERROR }

    private fun updateToolbarTitle(songCount: Int) = topLevelBehavior.defaultToolbar.updateToolbarTitle(
        R.string.shared_with_you,
        if (songCount == 0 && viewModel.state.value == StateLayout.State.LOADING) {
            getString(R.string.loading)
        } else {
            resources.getQuantityString(R.plurals.playlist_song_count, songCount, songCount)
        }
    )

    companion object {
        private var Bundle.intent by BundleArgumentDelegate.Parcelable("intent")

        fun newInstance(intent: Intent) = SharedWithYouFragment().withArguments {
            it.intent = intent
        }
    }
}