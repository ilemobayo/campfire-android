package com.pandulapeter.campfire.data.repository

import com.pandulapeter.campfire.data.model.Playlist
import com.pandulapeter.campfire.data.storage.DataStorageManager

/**
 * Wraps caching and updating of [Playlist] objects.
 */
class PlaylistRepository(private val dataStorageManager: DataStorageManager,
                         private val songInfoRepository: SongInfoRepository,
                         private val downloadedSongRepository: DownloadedSongRepository) : Repository() {

    fun getPlaylists() = dataStorageManager.getAllPlaylists()

    fun getPlaylist(playlistId: Int) = dataStorageManager.getPlaylist(playlistId)

    fun newPlaylist(name: String) {
        dataStorageManager.newPlaylist(name)
        notifySubscribers(UpdateType.PlaylistAddedOrRemoved)
    }

    fun getPlaylistSongs(playlistId: Int) = getPlaylist(playlistId).songIds
        .mapNotNull { songInfoRepository.getSongInfo(it) }
        .filter { downloadedSongRepository.isSongDownloaded(it.id) }

    fun isSongInPlaylist(songId: String): Boolean {
        getPlaylists().forEach {
            if (it.songIds.contains(songId)) {
                return true
            }
        }
        return false
    }

    fun isSongInPlaylist(playlistId: Int, songId: String) = getPlaylist(playlistId).songIds.contains(songId)

    fun addSongToPlaylist(playlistId: Int, songId: String, position: Int? = null) {
        if (!isSongInPlaylist(playlistId, songId)) {
            dataStorageManager.savePlaylist(getPlaylist(playlistId).apply {
                songIds.apply { if (position == null) add(songId) else add(position, songId) }
            })
            notifySubscribers()
        }
    }

    fun updatePlaylistTitle(playlistId: Int, title: String) {
        if (playlistId != Playlist.FAVORITES_ID) {
            dataStorageManager.savePlaylist(Playlist.Custom(playlistId, title, getPlaylist(playlistId).songIds))
            notifySubscribers()
        }
    }

    fun removeSongFromPlaylist(playlistId: Int, songId: String) {
        if (isSongInPlaylist(playlistId, songId)) {
            dataStorageManager.savePlaylist(getPlaylist(playlistId).apply {
                songIds.apply { remove(songId) }
            })
            notifySubscribers()
        }
    }

    fun deletePlaylist(playlistId: Int) {
        dataStorageManager.deletePlaylist(playlistId)
        notifySubscribers()
    }

    fun updatePlaylist(playlistId: Int, songIds: List<String>) {
        dataStorageManager.savePlaylist(getPlaylist(playlistId).apply {
            this.songIds.apply { swap(songIds) }
        })
        notifySubscribers()
    }

    fun shuffleFavorites() {
        //TODO: Re-implement this.
//        val list = dataStorageManager.favorites.toMutableList()
//        if (list.size > SHUFFLE_LIMIT) {
//            val newList = list.toMutableList()
//            while (newList == list) {
//                Collections.shuffle(newList)
//            }
//            dataStorageManager.favorites = newList
//            notifySubscribers()
//        }
    }

    companion object {
        private const val SHUFFLE_LIMIT = 2
    }
}