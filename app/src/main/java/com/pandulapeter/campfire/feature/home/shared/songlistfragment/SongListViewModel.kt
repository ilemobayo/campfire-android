package com.pandulapeter.campfire.feature.home.shared.songlistfragment

import com.pandulapeter.campfire.data.model.SongInfo
import com.pandulapeter.campfire.data.repository.DownloadedSongRepository
import com.pandulapeter.campfire.data.repository.PlaylistRepository
import com.pandulapeter.campfire.data.repository.SongInfoRepository
import com.pandulapeter.campfire.data.repository.UserPreferenceRepository
import com.pandulapeter.campfire.data.repository.shared.Subscriber
import com.pandulapeter.campfire.data.repository.shared.UpdateType
import com.pandulapeter.campfire.feature.home.shared.homefragment.HomeFragment
import com.pandulapeter.campfire.feature.home.shared.homefragment.HomeFragmentViewModel
import com.pandulapeter.campfire.feature.home.shared.songlistfragment.list.SongInfoAdapter
import com.pandulapeter.campfire.feature.home.shared.songlistfragment.list.SongInfoViewModel

/**
 * Parent class for view models that display lists of songs.
 *
 * Handles events and logic for subclasses of [SongListFragment].
 */
abstract class SongListViewModel(homeCallbacks: HomeFragment.HomeCallbacks?,
                                 private val userPreferenceRepository: UserPreferenceRepository,
                                 protected val songInfoRepository: SongInfoRepository,
                                 protected val downloadedSongRepository: DownloadedSongRepository,
                                 protected val playlistRepository: PlaylistRepository) : HomeFragmentViewModel(homeCallbacks), Subscriber {
    val adapter = SongInfoAdapter()
    protected var isAdapterNotEmpty = false

    abstract fun getAdapterItems(): List<SongInfoViewModel>

    override fun onUpdate(updateType: UpdateType) {
        val items = getAdapterItems()
        adapter.items = items
        isAdapterNotEmpty = items.isNotEmpty()
    }

    protected fun List<SongInfo>.filterWorkInProgress() = if (userPreferenceRepository.shouldHideWorkInProgress) filter { it.version ?: 0 >= 0 } else this

    protected fun List<SongInfo>.filterExplicit() = if (userPreferenceRepository.shouldHideExplicit) filter { it.isExplicit != true } else this
}