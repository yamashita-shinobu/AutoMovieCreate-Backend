package com.example.testgpttovox.service

import com.example.testgpttovox.dto.CreateVideoSettingDto
import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.entity.CreateVideoSettingEntity
import com.example.testgpttovox.repository.CreateVideoSettingRepository
import com.example.testgpttovox.repository.PromptTxtRepository
import com.example.testgpttovox.service.items.CommonMovieCreate
import com.example.testgpttovox.service.items.OpenAiClient
import com.example.testgpttovox.util.*
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MovieSettingService(
    private val createVideoSettingRepository: CreateVideoSettingRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {
    private val logger: Logger = LoggerFactory.getLogger(MovieSettingService::class.java)

    /**
     * 動画作成
     */
    fun selectSetting(userId: String): CreateVideoSettingDto {
        return logExecution("selectSetting") {
            val settings = createVideoSettingRepository.selectById(userId)

            CreateVideoSettingDto(
                chatGptVersion = settings.chatGptVersion,
                voiceVoxType = settings.voiceVoxType,
                voiceVoxNarration = settings.voiceVoxNarration,
            )
        }
    }

    fun updateSetting(
        chatGptVersion: String,
        voiceVoxType: String,
        voiceVoxNarration: String,
        userid: String
    ) {
        logExecution("updateSetting") {
            createVideoSettingRepository.update(
                CreateVideoSettingEntity(
                    userId = userid,
                    chatGptVersion = chatGptVersion,
                    voiceVoxType = voiceVoxType,
                    voiceVoxNarration = voiceVoxNarration,
                )
            )
        }
    }
}