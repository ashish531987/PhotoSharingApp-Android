package com.emergent.photosharingapp.ui

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.emergent.photosharingapp.utils.FileUtils
import com.emergent.photosharingapp.domain.Media
import com.emergent.photosharingapp.repository.MediaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {
    val REQ_CODE_GALLERY_IMAGE_CAPTURE = 1;
    var userId = MutableLiveData<Long>()
    private val repoResult = map(userId) {
        repository.getMedia(it, 2)
    }
    val posts = switchMap(repoResult, { it.pagedList })!!
    val networkState = switchMap(repoResult, { it.networkState })!!
    val refreshState = switchMap(repoResult, { it.refreshState })!!


    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }
    fun fabClicked(requestCode:Int, activity:AppCompatActivity){
        val pickPhoto = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto , requestCode)

    }

    val mediaDownloaded = MutableLiveData<Media>()

    fun onActivityResult(context : Context, requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        when(requestCode){
            REQ_CODE_GALLERY_IMAGE_CAPTURE->{
                if (resultCode == RESULT_OK) {
                    val uri = imageReturnedIntent?.getData()!!
                    val targetFile = FileUtils.copy(
                            context,
                            uri)

                    repository.uploadMedia(userId.value!!, targetFile, object : Callback<Media> {
                        override fun onFailure(call: Call<Media>, t: Throwable) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onResponse(call: Call<Media>, response: Response<Media>) {
                            if(response.isSuccessful){
                                mediaDownloaded.value = response.body()
                                refresh()
                            }
                        }

                    })
                }
            }
        }
    }

    fun likeMedia(media: Media) {
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
        if(media.likedByMe){
            repository.unlikeMedia(userId.value!!, media.id, callback)
        } else {
            repository.likeMedia(userId.value!!, media.id, callback)
        }
    }
}
