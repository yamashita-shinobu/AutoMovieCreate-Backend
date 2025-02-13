package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

/**
 * ffmpegのストリームタイプ
 * @param code コード
 * @param value 値
 * @exception CodeValueEnum ENUMの共通クラス
 */
enum class StreamType(
    override val code: String,
    override val value: String
): CodeValueEnum {
    VIDEO("video","v"),
    AUDIO("audio","a"),
    SUBTITLE("subtitle","s");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<StreamType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<StreamType>(value)
        }
    }
}