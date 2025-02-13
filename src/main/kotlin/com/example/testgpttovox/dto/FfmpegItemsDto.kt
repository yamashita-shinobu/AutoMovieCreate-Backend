package com.example.testgpttovox.dto

import java.io.File

data class FfmpegItemsDto(
    // ffmpegのinput
    val inputs: List<String>,
    // ffmpegのoutput
    val output: String,
    // ffmpegのログファイル
    val logFile: File,
    // ナレーションの総時間
    val narrationLength: Double,
)
