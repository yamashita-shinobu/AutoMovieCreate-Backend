package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "audios", schema="mstknr")
data class AudiosEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "audio_name")
    val audioName: String,

    @Column(name = "audio_data")
    val audioData: ByteArray,
)
