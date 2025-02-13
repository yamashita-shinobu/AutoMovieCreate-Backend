package com.example.testgpttovox.dto


data class CreateVideoSettingDto(

    val chatGptVersion: String,

    val voiceVoxType: String,

    val voiceVoxNarration: String,

)
