package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "videos", schema="mstknr")
data class VideosEntity(

    @Id
    val id: Int,

    @Column(name = "video_name")
    val videoName: String,

    @Column(name = "video_data")
    val videoData: ByteArray,
)
