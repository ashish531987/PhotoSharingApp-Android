package com.emergent.photosharingapp.domain

import java.util.*

data class Comments(
        val id : Long,
        val user : User,
        val comment : String,
        val commentedAt : String
)