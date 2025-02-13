package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "create_video_samune_info", schema="mstknr")
data class CreateVideoSamuneInfoEntity(

    @Id
    @Column(name = "userid")
    val userId: String,

    @Id
    @Column(name = "video_id")
    val videoId: String,

    @Column(name = "title")
    val title: String,

    @Column(name = "image_data")
    val imageData: ByteArray,
)
