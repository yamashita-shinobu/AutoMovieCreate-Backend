package com.example.testgpttovox.service

import com.example.testgpttovox.util.ConstantList
import com.example.testgpttovox.util.MsgSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseService(
    val messageSource: MsgSource,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected fun <T> logExecution(methodName: String, block: () -> T): T {
        logger.info(
            messageSource.getMessageSource(
                ConstantList.MSG_I_001,
                arrayOf(methodName)
            )
        )
        val result = block()
        logger.info(
            messageSource.getMessageSource(
                ConstantList.MSG_I_002,
                arrayOf(methodName)
            )
        )
        return result
    }
}