package com.example.testgpttovox.dto

data class AudioInsertInputDto(
    val audioName: String,
    val audioData: ByteArray,
)
