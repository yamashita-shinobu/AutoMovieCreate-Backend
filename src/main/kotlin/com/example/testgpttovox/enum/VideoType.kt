package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class VideoType(override val code: String, override val value: String): CodeValueEnum {
    PC("pc", "0"),
    MOBILE("mobile", "1");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<VideoType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<VideoType>(value)
        }
    }
}