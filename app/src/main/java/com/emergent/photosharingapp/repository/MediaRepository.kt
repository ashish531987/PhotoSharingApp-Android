package com.emergent.photosharingapp.repository

import android.net.Uri
import com.emergent.photosharingapp.domain.Media
import retrofit2.Callback
import java.io.File

interface MediaRepository {
    fun getMedia(userId: String, pageSize: Int): Listing<Media>
    fun uploadMedia(userId: String, file: File, callback: Callback<Media>)
    fun likeMedia(userId: String, mediaId: Long, callback: Callback<Media>)
    fun unlikeMedia(userId: String, mediaId: Long, callback: Callback<Media>)
}
