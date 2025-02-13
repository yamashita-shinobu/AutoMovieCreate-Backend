package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class PresetType(override val code: String, override val value: String): CodeValueEnum {
    ULTRA_FAST("ultrafast", "ultrafast"),
    SUPER_FAST("superfast", "superfast"),
    VERY_FAST("veryfast", "veryfast"),
    FASTER("faster", "faster"),
    FAST("fast", "fast"),
    MEDIUM("medium", "medium"),
    SLOW("slow", "slow"),
    SLOWER("slower", "slower"),
    VERY_SLOW("veryslow", "veryslow"),
    PLACEBO("placebo", "placebo");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<PresetType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<PresetType>(value)
        }
    }
}