package com.example.testgpttovox.util

object EnumHelper {
    inline fun <reified T> toValue(code:String): String where T: Enum<T>, T: CodeValueEnum {
        return enumValues<T>().firstOrNull {it.code == code}?.value ?: code
    }

    inline fun <reified T> toCode(value:String): String where T: Enum<T>, T: CodeValueEnum {
        return enumValues<T>().firstOrNull {it.value == value}?.code ?: value
    }

    inline fun <reified T> toVal(code:String): String where T: Enum<T>, T: CodeValueCreditEnum {
        return enumValues<T>().firstOrNull {it.code == code}?.value ?: code
    }

    inline fun <reified T> toCd(value:String): String where T: Enum<T>, T: CodeValueCreditEnum {
        return enumValues<T>().firstOrNull {it.value == value}?.code ?: value
    }

    inline  fun <reified  T> toCredit(code: String): String where T: Enum<T>, T:CodeValueCreditEnum {
        return enumValues<T>().firstOrNull {it.code == code}?.credit ?: code
    }
}