package com.emergent.photosharingapp.domain

data class ExceptionResponseDTO(
        val timestamp : String,
        val message : String,
        val details : String
)