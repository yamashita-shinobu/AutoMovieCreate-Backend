package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class MediaType(override val code: String, override val value: String): CodeValueEnum {
    JSON("json", "application/json; charset=utf-8");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<MediaType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<MediaType>(value)
        }
    }
}