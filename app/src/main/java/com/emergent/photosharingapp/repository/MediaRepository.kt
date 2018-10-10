package com.emergent.photosharingapp.repository

import com.emergent.photosharingapp.domain.Media

interface MediaRepository {
    fun getMedia(userId: String, pageSize: Int): Listing<Media>
}
