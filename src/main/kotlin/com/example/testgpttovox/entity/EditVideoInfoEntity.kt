package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "edit_video_info", schema="mstknr")
data class EditVideoInfoEntity(

    @Id
    @Column(name = "user_id")
    val userId: String,

    @Id
    @Column(name = "video_id")
    val videoId: String,

    @Column(name = "bms_name")
    val bmsName: String,

    @Column(name = "cmt_frame_name")
    val cmtFrameName: String,

    @Column(name = "bgm_name")
    val bgmName: String,

    @Column(name = "comments")
    val comments: String,

    @Column(name = "main_images")
    val mainImages: String,
)
