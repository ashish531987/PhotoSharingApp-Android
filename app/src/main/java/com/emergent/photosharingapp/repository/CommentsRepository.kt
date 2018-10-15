package com.emergent.photosharingapp.repository

import com.emergent.photosharingapp.domain.Comments
import com.emergent.photosharingapp.domain.Media
import retrofit2.Callback

interface CommentsRepository {
    fun getCommentsForMedia(userId: Long, mediaId: Long, pageSize: Int): Listing<Comments>
    fun commentOnMedia(userId: Long, mediaId: Long, comment : String, callback: Callback<Media>)
}
