package com.example.testgpttovox.service.items

import com.example.testgpttovox.dto.CommentInfoDto
import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.dto.VideoInfoDto
import com.example.testgpttovox.entity.CreateVideoInfoEntity
import com.example.testgpttovox.enum.VoiceVoxType
import com.example.testgpttovox.exceptions.FileCreateException
import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.service.GetImageService
import com.example.testgpttovox.util.ConstantList
import com.example.testgpttovox.util.StringUtils.Companion.pathToString
import com.example.testgpttovox.util.StringUtils.Companion.strToByteArrayAndUtf8
import com.example.testgpttovox.util.StringUtils.Companion.stringToPath
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.*
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class CommonMovieCreate(
    @Value("\${create.create-path}") val createPath: String,
    val voiceVoxClient: VoiceVoxClient,
    val editMovie: EditMovie,
    val getImageService: GetImageService,
    val createVideoInfoRepository: CreateVideoInfoRepository,
) {

    @Transactional
    fun movieCommon(videoInfoDto: VideoInfoDto, searchTarget: String, userId: String, editMovieInfoDto: EditMovieInfoDto?): String {
        // 作業フォルダの作成
        val currentDateTime = LocalDateTime.now()
        val yyyyMMddhhmmssSSS = DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS")
        val formatTime = currentDateTime.format(yyyyMMddhhmmssSSS)

        val userDir = createPath+userId + "\\"
        if(!Files.exists(Path.of(userDir))){
            Files.createDirectory(Path.of(userDir))
        }

        val putImagePath = if(editMovieInfoDto == null){
            pathToString(userDir, formatTime)
        }else{
            pathToString(userDir, editMovieInfoDto.videoId)
        }

        if(editMovieInfoDto == null) {
            Files.createDirectory(Path.of(putImagePath))
            // 画像の取得
            getImageService.getImage(searchTarget, putImagePath)
        }

        val tempFolder = stringToPath(ConstantList.RESOURCE_PATH, formatTime)
        Files.createDirectory(tempFolder)
        // voiceVoxの音声ファイル格納ディレクトリ
        val voiceVoxPath = stringToPath(tempFolder.toString(), ConstantList.DIR_VOICEVOX_NAME)
        Files.createDirectory(voiceVoxPath)

        if(editMovieInfoDto == null){
            createVideoInfoRepository.insert(
                CreateVideoInfoEntity(
                    userId = userId,
                    videoId = formatTime,
                    title = videoInfoDto.title,
                    description = videoInfoDto.description,
                    tag = videoInfoDto.tag.joinToString(separator = ConstantList.STR_COMMA),
                    scenario = videoInfoDto.scenario
                )
            )
        }else{
            createVideoInfoRepository.update(
                CreateVideoInfoEntity(
                    userId = userId,
                    videoId = formatTime,
                    title = videoInfoDto.title,
                    description = videoInfoDto.description,
                    tag = videoInfoDto.tag.joinToString(separator = ConstantList.STR_COMMA),
                    scenario = editMovieInfoDto.sceneItems.joinToString("") {
                        it.comment.joinToString("")
                    }
                )
            )
        }

        try {
            val strVoiceVoxPath = voiceVoxPath.toString()
            val voiceFilePath = pathToString(strVoiceVoxPath, ConstantList.FILE_VOICE)
            val editComment = editMovieInfoDto?.sceneItems?.map {
                it.comment
            }
            //　分割したテキストをもとに音声ファイルの作成。そのメタ情報を取得
            val commentInfoDtoList = voiceCreateStart(videoInfoDto, strVoiceVoxPath, voiceFilePath, editComment)
            val tempFilePath = pathToString(tempFolder.toString(), ConstantList.DIR_CREATE_VIDEO)
            // 動画作成開始
            editMovie.startCreate(strVoiceVoxPath, commentInfoDtoList, tempFilePath, voiceFilePath, videoInfoDto,  formatTime, userId, editMovieInfoDto, putImagePath)

            // 説明文テキスト作成
            val videoInfoTxt = stringToPath(tempFilePath, "動画情報.txt")
            val writeTxt = ConstantList.TXT_DESCRIPTION.format(
                videoInfoDto.title,
                videoInfoDto.description,
                videoInfoDto.tag.joinToString { it },
                VoiceVoxType.toCredit("ずんだもん")
            ).trimIndent()
            Files.write(videoInfoTxt, strToByteArrayAndUtf8(writeTxt))

       } catch (ex: FileCreateException) {
            throw FileCreateException(ex.message ?: ConstantList.STR_BLANK)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                deleteDirectoryRecursively(tempFolder)
                println("ディレクトリが削除されました: $tempFolder")
            } catch (e: Exception) {
                e.printStackTrace()
                println("ディレクトリの削除に失敗しました: $tempFolder")
            }
        }

        val videoId = editMovieInfoDto?.videoId ?: formatTime

        return videoId
    }

    private fun voiceCreateStart(
        videoInfoDto: VideoInfoDto,
        voiceVoxPath: String,
        voiceFilePath: String,
        editComment: List<List<String>>?
    ): List<CommentInfoDto> {
        // 正規表現で分割すると最後の配列が空が返されるため削除しておく
        val splitScenario = videoInfoDto.scenario.split("(?<=[。！？!?])".toRegex()).dropLast(1)

        //　分割したテキストをもとに音声ファイルの作成。その情報を取得
        val fileName = "voicevox%s.wav"
        val commentInfoDtoList =
            voiceVoxClient.createListAudio(splitScenario, voiceVoxPath, fileName, videoInfoDto.voiceVoxChara, videoInfoDto.voiceVoxType, editComment)

        //　音声ファイルを一つに結合
        voiceVoxClient.plusAudio(commentInfoDtoList, File(voiceFilePath))

        return commentInfoDtoList
    }

    private fun deleteDirectoryRecursively(path: Path) {
        if (Files.isDirectory(path)) {
            // ディレクトリ内の全てのファイルとサブディレクトリを削除
            Files.list(path).forEach { subPath ->
                deleteDirectoryRecursively(subPath)
            }
        }
        // 最後にディレクトリ自体を削除
        Files.delete(path)
    }
}