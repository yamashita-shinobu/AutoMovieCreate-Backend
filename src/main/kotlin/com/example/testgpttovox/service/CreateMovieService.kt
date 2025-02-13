package com.example.testgpttovox.service

import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.enum.ChatGPTType
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
class CreateMovieService(
    val commonMovieCreate: CommonMovieCreate,
    val openAiClient: OpenAiClient,
    val promptTxtRepository: PromptTxtRepository,
    val createVideoSettingRepository: CreateVideoSettingRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {
    private val logger: Logger = LoggerFactory.getLogger(CreateMovieService::class.java)

    /**
     * 動画作成
     */
    fun createMovie(createTarget: String, searchTarget: String, userId: String): String {
        return  logExecution("CreateMovieService_createMovie") {
            val settings = createVideoSettingRepository.selectById(userId)
            // プロンプトの作成
            val prompt = promptTxtRepository.select_primary(1)

            val chatGPTVersion = ChatGPTType.toCode(settings.chatGptVersion)
            val message = openAiClient.getCompletion(
                prompt.prompt.trimIndent().format(createTarget),
                1000,
                chatGPTVersion
            )
            val videoInfoDto = Gson().fromJson(message, VideoInfoDto::class.java)

            // 説明文の作成
            val descriptionPrompt = promptTxtRepository.select_primary(2)

            val message2 = openAiClient.getCompletion(
                descriptionPrompt.prompt.trimIndent().format(videoInfoDto.title),
                1000,
                chatGPTVersion
            )
            val videoInfoDto2 =
                Gson().fromJson(message2, VideoInfoDto::class.java)

            val imageTarget = if(searchTarget != ""){
                searchTarget
            } else {
                createTarget
            }

            commonMovieCreate.movieCommon(
                VideoInfoDto(
                    title = videoInfoDto.title,
                    scenario = videoInfoDto.scenario,
                    description = videoInfoDto2.description,
                    tag = videoInfoDto2.tag,
                    voiceVoxType = settings.voiceVoxType,
                    voiceVoxChara = settings.voiceVoxNarration,
                ),
                imageTarget,
                userId,
                null
            )
        }
    }
}