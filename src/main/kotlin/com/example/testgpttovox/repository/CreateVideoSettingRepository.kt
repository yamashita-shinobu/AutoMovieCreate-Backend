package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.CreateVideoSettingEntity
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
interface CreateVideoSettingRepository {
    @Insert
    fun insert(videoInfo: CreateVideoSettingEntity): Result<CreateVideoSettingEntity>

    @Update
    fun update(videoSetInfo: CreateVideoSettingEntity):Result<CreateVideoSettingEntity>

    @Select
    fun selectAll(): List<CreateVideoSettingEntity>

    @Select
    fun selectById(id: String): CreateVideoSettingEntity

    @Select
    fun selectExists(id: String): Boolean
}