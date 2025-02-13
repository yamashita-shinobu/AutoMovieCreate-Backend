package com.example.testgpttovox.entity

import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

@Entity(immutable = true)
@Table(name = "PROMPT_TXT", schema="mstknr")
data class PromptTxtEntity(

    /** id */
    @Id
    val id: Int,

    /** プロンプト */
    val prompt: String,

    /** 種類 */
    val type: String,
)
