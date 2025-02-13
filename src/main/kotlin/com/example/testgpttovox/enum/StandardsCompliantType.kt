package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class StandardsCompliantType(override val code: String, override val value: String): CodeValueEnum {
    // 実験的な機能を許可: 実験的とされる機能やコーデックの使用を許可します。例えば、libfdk_aacエンコーダは非常に高品質なAACエンコーダですが、FFmpegでは「実験的」として扱われるため、このオプションを指定しないと使用できません
    ESPERMENTAL("-2", "experimental"),
    // 非公式な機能を許可: 標準に準拠していない非公式な機能やコーデックの使用を許可します。実験的な機能よりもさらに制約が緩い設定です。
    UNOFFICIAL("-1", "unofficial"),
    // デフォルトのモード: 標準に準拠した動作を行いますが、特定の実験的または非公式な機能を無効にすることはありません。これはFFmpegのデフォルトの挙動です。
    NORMAL("0", "normal"),
    // 厳格な準拠: 標準に厳密に準拠し、実験的または非公式な機能の使用を禁止します。これにより、互換性を重視したエンコードが行われますが、FFmpegが提供する一部の高度な機能は使用できなくなります。
    STRICT("1", "strict"),
    // 非常に厳格な準拠: 標準に対して非常に厳格に準拠します。ほとんどの非公式な機能や標準外の設定を無効にします。特定の用途で、標準に厳密に従う必要がある場合に使用されます。
    verystrict("2", "verystrict");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<StandardsCompliantType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<StandardsCompliantType>(value)
        }
    }
}