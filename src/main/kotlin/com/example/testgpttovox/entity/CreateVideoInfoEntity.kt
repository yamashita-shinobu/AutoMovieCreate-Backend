package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "create_video_info", schema="mstknr")
data class CreateVideoInfoEntity(

    @Id
    @Column(name = "userid")
    val userId: String,

    @Id
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
)
