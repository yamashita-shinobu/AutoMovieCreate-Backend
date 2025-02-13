package com.example.testgpttovox.service

import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SelectVideoService(
    val createVideoInfoRepository: CreateVideoInfoRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * 動画登録
     */
    @Transactional
    fun select(userId: String, videoId: String): List<Map<String, Any>> {
        return logExecution("SelectVideoService_select") {
            val video = createVideoInfoRepository.selectVideoAndInfo(userId, videoId)

            val response = listOf(
                mapOf(
                    "filetype" to "video/mp4",
                    "data" to Base64.getEncoder().encodeToString(video.videoData),
                    "title" to video.title,
                    "description" to video.description,
                    "scenario" to video.scenario,
                    "tag" to video.tag
                ), mapOf(
                    "filetype" to "video/mp4",
                    "data" to Base64.getEncoder().encodeToString(video.mobileVideoData),
                )
            )

            response
        }
    }
}