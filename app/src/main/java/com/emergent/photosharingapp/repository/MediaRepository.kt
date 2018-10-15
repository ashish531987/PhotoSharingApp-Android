package com.emergent.photosharingapp.repository

import android.net.Uri
import com.emergent.photosharingapp.domain.Media
import retrofit2.Callback
import java.io.File

interface MediaRepository {
    fun getMedia(userId: Long, pageSize: Int): Listing<Media>
    fun uploadMedia(userId: Long, file: File, callback: Callback<Media>)
    fun likeMedia(userId: Long, mediaId: Long, callback: Callback<Media>)
    fun unlikeMedia(userId: Long, mediaId: Long, callback: Callback<Media>)
}
