package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "create_video_setting", schema="mstknr")
data class CreateVideoSettingEntity(

    @Id
    @Column(name = "userid")
    val userId: String,

    @Column(name = "chat_gpt_version")
    val chatGptVersion: String,

    @Column(name = "voice_vox_type")
    val voiceVoxType: String,

    @Column(name = "voice_vox_narration")
    val voiceVoxNarration: String,

)
