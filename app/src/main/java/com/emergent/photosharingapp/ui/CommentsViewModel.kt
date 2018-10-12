package com.emergent.photosharingapp.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.CommentsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsViewModel(private val repository: CommentsRepository) : ViewModel() {
    var userId = MutableLiveData<String>()
    var mediaId = MutableLiveData<Long>()
    private val repoResult = map(mediaId) {
        repository.getCommentsForMedia(userId.value.orEmpty(), it, 2)
    }
    val posts = switchMap(repoResult) { it.pagedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!


    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    private fun commentOnMedia(mediaId: Long, comment : String) {
        val callback = object : Callback<Media> {
            override fun onFailure(call: Call<Media>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Media>, response: Response<Media>) {
                if(response.isSuccessful){
                    refresh()
                }
            }

        }
        repository.commentOnMedia(userId.value!!, mediaId, comment, callback)
    }

    fun commentIBClicked(comment:String) {
        commentOnMedia(mediaId.value!!, comment)
    }
}
