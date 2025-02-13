package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.entity.VideosEntity
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
interface CreateVideoSamuneInfoRepository {
    @Insert
    fun insert(videoInfo: CreateVideoSamuneInfoEntity): Result<CreateVideoSamuneInfoEntity>

    @Update
    fun update(videoInfo: CreateVideoSamuneInfoEntity): Result<CreateVideoSamuneInfoEntity>

    @Delete
    fun delete(videoInfo: CreateVideoSamuneInfoEntity): Result<CreateVideoSamuneInfoEntity>

    @Select
    fun selectAll(): List<CreateVideoSamuneInfoEntity>

    @Select
    fun selectUserAndVideoOfId(userId: String, videoId: String): CreateVideoSamuneInfoEntity

    @Select
    fun selectByUserId(userId: String): List<CreateVideoSamuneInfoEntity>

    @Select
    fun selectExists(userId: String, videoId: String): Boolean
}