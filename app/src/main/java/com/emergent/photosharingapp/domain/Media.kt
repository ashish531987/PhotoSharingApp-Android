package com.emergent.photosharingapp.domain

data class Media(val id : Long,
                 val user: User,
                 val downloadURI: String,
                 val likeCount: Int,
                 val commentCount: Int,
                 val caption: String
)