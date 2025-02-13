package com.example.testgpttovox.service

import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.enum.VoiceVoxType
import com.example.testgpttovox.exceptions.FileCreateException
import com.example.testgpttovox.repository.CreateVideoSamuneInfoRepository
import com.example.testgpttovox.repository.PromptTxtRepository
import com.example.testgpttovox.repository.VideosRepository
import com.example.testgpttovox.service.items.CommonMovieCreate
import com.example.testgpttovox.service.items.EditMovie
import com.example.testgpttovox.service.items.OpenAiClient
import com.example.testgpttovox.service.items.VoiceVoxClient
import com.example.testgpttovox.util.*
import com.example.testgpttovox.util.StringUtils.Companion.pathToString
import com.example.testgpttovox.util.StringUtils.Companion.stringToPath
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

@Service
class GetVideoSamuneService(
    val createVideoSamuneInfoRepository: CreateVideoSamuneInfoRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * フォルダ一覧取得
     */
    fun getSamune(userId: String): List<CreateVideoSamuneInfoEntity>{
        return logExecution("getSamune") {
            createVideoSamuneInfoRepository.selectByUserId(userId)
        }
    }
}