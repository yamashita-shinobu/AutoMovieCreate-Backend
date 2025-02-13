package com.example.testgpttovox.service

import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.repository.CreateVideoSettingRepository
import com.example.testgpttovox.service.items.CommonMovieCreate
import com.example.testgpttovox.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ReCreateMovieService(
    @Value("\${create.create-path}") val createPath: String,
    val commonMovieCreate: CommonMovieCreate,
    val createVideoInfoRepository: CreateVideoInfoRepository,
    val createVideoSettingRepository: CreateVideoSettingRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * 動画作成
     */
    fun reCreateMovie(editMovieInfoDto: EditMovieInfoDto): String {
        return logExecution("reCreateMovie") {
            val settings = createVideoSettingRepository.selectById(editMovieInfoDto.userId)
            // DBから動画情報取得
            val videoInfoEntity = createVideoInfoRepository.selectById(editMovieInfoDto.userId, editMovieInfoDto.videoId)

            commonMovieCreate.movieCommon(
                VideoInfoDto(
                    title = videoInfoEntity.title,
                    description = videoInfoEntity.description,
                    scenario = videoInfoEntity.scenario,
                    tag = videoInfoEntity.tag.split(ConstantList.STR_COMMA),
                    voiceVoxType = settings.voiceVoxType,
                    voiceVoxChara = settings.voiceVoxNarration,
                ),"",editMovieInfoDto.userId,editMovieInfoDto
            )
        }
    }
}