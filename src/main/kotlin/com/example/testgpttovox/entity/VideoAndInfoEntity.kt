package com.example.testgpttovox.entity

import org.seasar.doma.Column
import org.seasar.doma.Entity

@Entity(immutable = true)
data class VideoAndInfoEntity(
    @Column(name = "video_id")
    val videoId: String,

    @Column(name = "title")
    val title: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "tag")
    val tag: String,

    @Column(name = "scenario")
    val scenario: String,

    @Column(name = "pc_video")
    val videoData: ByteArray,

    @Column(name = "mobile_video")
    val mobileVideoData: ByteArray,
)
