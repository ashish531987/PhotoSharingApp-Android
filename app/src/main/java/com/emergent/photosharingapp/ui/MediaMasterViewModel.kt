package com.emergent.photosharingapp.ui

import android.arch.lifecycle.*
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.support.v7.app.AppCompatActivity

import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.Listing
import com.emergent.photosharingapp.repository.MediaRepository

class MediaMasterViewModel(private val repository: MediaRepository) : ViewModel() {
    private val subredditName = MutableLiveData<String>()
    private val repoResult = map(subredditName) {
        repository.getMedia(it, 2)
    }
    val posts = switchMap(repoResult, { it.pagedList })!!
    val networkState = switchMap(repoResult, { it.networkState })!!
    val refreshState = switchMap(repoResult, { it.refreshState })!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }
    fun showSubreddit(subreddit: String): Boolean {
        if (subredditName.value == subreddit) {
            return false
        }
        subredditName.value = subreddit
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }
}
