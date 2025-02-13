package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "create_videos", schema="mstknr")
data class CreateVideosEntity(

    @Id
    @Column(name = "userid")
    val userId: String,

    @Id
    @Column(name = "video_id")
    val videoId: String,

    @Column(name = "pc_video")
    val videoData: ByteArray,

    @Column(name = "mobile_video")
    val mobileVideoData: ByteArray,
)
