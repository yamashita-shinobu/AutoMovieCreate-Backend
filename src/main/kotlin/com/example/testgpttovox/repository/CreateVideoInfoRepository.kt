package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.entity.VideoAndInfoEntity
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Update
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result
import org.springframework.stereotype.Repository

@Dao
@Repository
@ConfigAutowireable
interface CreateVideoInfoRepository {
    @Insert
    fun insert(videoInfo: CreateVideoInfoEntity): Result<CreateVideoInfoEntity>

    @Update
    fun update(videoInfo: CreateVideoInfoEntity): Result<CreateVideoInfoEntity>

    @Select
    fun selectById(userId: String, videoId: String): CreateVideoInfoEntity

    @Select
    fun selectAll(): List<CreateVideoInfoEntity>

    @Select
    fun selectVideoAndInfo(userId: String, videoId: String): VideoAndInfoEntity
}