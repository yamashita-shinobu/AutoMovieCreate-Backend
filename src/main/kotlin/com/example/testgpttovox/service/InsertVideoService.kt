package com.example.testgpttovox.service

import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.repository.VideosRepository
import com.example.testgpttovox.util.*
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class InsertVideoService(
    @Value("\${create.create-path}") val createPath: String,
    messageSource: MsgSource,
    val createVideoInfoRepository: CreateVideoInfoRepository
) : BaseService(messageSource) {

    /**
     * 動画登録
     */
    @Transactional
    fun insert() {
        logExecution("InsertVideoService_insert") {
            File(Paths.get(createPath).toString()).list()?.forEach {
                val defalutPath = createPath + it
                val insertFile =  File("$defalutPath\\finalVideo.mp4").readBytes()
                val insertMobileFile =  File("$defalutPath\\mobile_final_video.mp4").readBytes()
                val jsonVideoInfo = Files.readString(Paths.get("$defalutPath\\動画情報.json"))
                val videoInfoDto =  Gson().fromJson(jsonVideoInfo, VideoInfoDto::class.java)

                createVideoInfoRepository.insert(
                    CreateVideoInfoEntity(
                        userId = "00000000000000000000",
                        videoId = it,
                        title = videoInfoDto.title,
                        description = videoInfoDto.description,
                        tag = videoInfoDto.tag.joinToString(separator = ","),
//                        videoData = insertFile,
//                        mobileVideoData = insertMobileFile,
                        scenario = videoInfoDto.scenario
                    )
                )
            }
        }
    }
}