package com.example.testgpttovox.service

import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.repository.CreateVideoSamuneInfoRepository
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
class InsertImageService(
    @Value("\${create.create-path}") val createPath: String,
    messageSource: MsgSource,
    val createVideoSamuneInfoRepository: CreateVideoSamuneInfoRepository
) : BaseService(messageSource) {

    /**
     * 動画登録
     */
    @Transactional
    fun insert() {
        logExecution("InsertImageService_insert") {

            File(Paths.get(createPath).toString()).list()?.forEach {
                val defalutPath = createPath + it
                val insertFile =  File("$defalutPath\\photo_0.jpg").readBytes()
                val jsonVideoInfo = Files.readString(Paths.get("$defalutPath\\動画情報.json"))
                val videoInfoDto =  Gson().fromJson(jsonVideoInfo, VideoInfoDto::class.java)

                createVideoSamuneInfoRepository.insert(
                    CreateVideoSamuneInfoEntity(
                        userId = "000000000000000000000",
                        videoId = it,
                        title = videoInfoDto.title,
                        imageData = insertFile,
                    )
                )
            }
        }
    }
}