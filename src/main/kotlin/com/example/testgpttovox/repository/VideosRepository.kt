package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.VideosEntity
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.seasar.doma.jdbc.Result
import org.springframework.stereotype.Repository

@Dao
@Repository
@ConfigAutowireable
interface VideosRepository {
    @Insert
    fun insert(videos: VideosEntity): Result<VideosEntity>

    @Select
    fun selectById(id: String): VideosEntity
}