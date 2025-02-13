package com.example.testgpttovox.entity

import org.seasar.doma.*

@Entity(immutable = true)
@Table(name = "user_info", schema="mstknr")
data class UserInfoEntity(

    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "user_name")
    val userName: String,

    @Column(name = "email")
    val email: String,
)
