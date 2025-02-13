package com.example.testgpttovox.dto

data class EditMovieInfoDto(
    val userId: String,
    val videoId: String,
    val bmsImage: String,
    val bmsName: String,
    val cmtFrameImage: String,
    val cmtFrameName: String,
    val bgmName: String,
    val sceneItems: List<EditMovieSeneInfoDto>,
)
