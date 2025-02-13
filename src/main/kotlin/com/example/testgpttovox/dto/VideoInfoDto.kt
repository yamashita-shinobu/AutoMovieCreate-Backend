package com.example.testgpttovox.dto

import com.example.testgpttovox.enum.VoiceVoxType

data class VideoInfoDto(
    val title: String,
    val scenario: String,
    val description: String,
    val tag: List<String>,
    val voiceVoxType: String,
    val voiceVoxChara: String,
)
