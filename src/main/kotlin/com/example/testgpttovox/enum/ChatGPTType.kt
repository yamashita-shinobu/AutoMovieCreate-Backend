package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class ChatGPTType(override val code: String, override val value: String): CodeValueEnum {
    TURBO("gpt-3.5-turbo", "0"),
    GPT4("gpt-4", "1"),
    GPT4OMINI("gpt-4o-mini", "2"),
    GPT4O("gpt-4o", "3");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<ChatGPTType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<ChatGPTType>(value)
        }
    }
}