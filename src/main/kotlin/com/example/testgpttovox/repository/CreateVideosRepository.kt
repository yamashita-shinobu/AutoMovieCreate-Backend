package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.CreateVideosEntity
import org.seasar.doma.Dao
import org.seasar.doma.Delete
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result
import org.springframework.stereotype.Repository

@Dao
@Repository
@ConfigAutowireable
interface CreateVideosRepository {
    @Insert
    fun insert(videoInfo: CreateVideosEntity): Result<CreateVideosEntity>

    @Update
    fun update(videoInfo: CreateVideosEntity): Result<CreateVideosEntity>

    @Delete
    fun delete(videoInfo: CreateVideosEntity): Result<CreateVideosEntity>

    @Select
    fun selectById(userId: String, videoId: String): CreateVideosEntity

    @Select
    fun selectByUser(userId: String): List<CreateVideosEntity>

    @Select
    fun selectExists(userId: String, videoId: String): Boolean
}