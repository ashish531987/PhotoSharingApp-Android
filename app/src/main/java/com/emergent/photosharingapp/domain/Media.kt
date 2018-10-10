package com.emergent.photosharingapp.domain

import android.graphics.drawable.Drawable

data class Media(val id : Long,
                 val user: User,
                 val downloadURI: String,
                 val likeCount: Int,
                 val commentCount: Int,
                 val caption: String
)