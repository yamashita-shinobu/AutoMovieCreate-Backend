package com.example.testgpttovox.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

/**
 * MessageSourceカスタムクラス
 */
@Component
class MsgSource {

    // デフォルトMessageSource
    @Autowired
    lateinit var messageSource: MessageSource

    /**
     * メッセージプロパティからメッセージ取得
     * @param code 取得するコード
     * @param args 組み込み文字配列
     */
    fun getMessageSource(code: String, args: Array<Any>): String{
        return messageSource.getMessage(code, args, Locale.JAPANESE)
    }
}