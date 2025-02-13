package com.example.testgpttovox.repository

import com.example.testgpttovox.entity.PromptTxtEntity
import org.seasar.doma.Dao
import org.seasar.doma.Select
import org.seasar.doma.boot.ConfigAutowireable
import org.springframework.stereotype.Repository

@Dao
@Repository
@ConfigAutowireable
interface PromptTxtRepository {
    @Select
    fun select_primary(id: Int):PromptTxtEntity
}