package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.entity.EditVideoInfoEntity
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
interface EditVideoInfoRepository {
    @Insert
    fun insert(editVideoInfoEntity: EditVideoInfoEntity): Result<EditVideoInfoEntity>

    @Update
    fun update(editVideoInfoEntity: EditVideoInfoEntity): Result<EditVideoInfoEntity>

    @Select
    fun selectAll(): List<EditVideoInfoEntity>

    @Select
    fun selectByKey(userId: String, videoId: String): EditVideoInfoEntity
}