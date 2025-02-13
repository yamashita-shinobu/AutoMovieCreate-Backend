package com.example.testgpttovox.service.items

import com.example.testgpttovox.dto.*
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.entity.CreateVideosEntity
import com.example.testgpttovox.entity.EditVideoInfoEntity
import com.example.testgpttovox.enum.*
import com.example.testgpttovox.repository.CreateVideoSamuneInfoRepository
import com.example.testgpttovox.repository.CreateVideosRepository
import com.example.testgpttovox.repository.EditVideoInfoRepository
import com.example.testgpttovox.service.BaseService
import com.example.testgpttovox.util.ConstantList
import com.example.testgpttovox.util.MsgSource
import com.example.testgpttovox.util.StringUtils.Companion.canMerge
import com.example.testgpttovox.util.StringUtils.Companion.getItemPath
import com.example.testgpttovox.util.StringUtils.Companion.pathToString
import com.example.testgpttovox.util.StringUtils.Companion.strPathToFileByte
import com.example.testgpttovox.util.StringUtils.Companion.strToAbsolutePath
import com.example.testgpttovox.util.StringUtils.Companion.strToFile
import com.example.testgpttovox.util.StringUtils.Companion.stringToPath
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.opencv.core.*
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio.CAP_PROP_FPS
import org.opencv.videoio.Videoio.CAP_PROP_FRAME_COUNT
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.math.ceil

/**
 * 動画作成メインクラス
 * @author 山下 忍
 */
@Service
class EditMovie(
    @Value("\${create.items-path}") val itemsPath: String,
    messageSource: MsgSource,
    val voiceVoxClient: VoiceVoxClient,
    val createVideosRepository: CreateVideosRepository,
    val createVideoSamuneInfoRepository: CreateVideoSamuneInfoRepository,
    val editVideoInfoRepository: EditVideoInfoRepository,
) : BaseService(messageSource) {
    private val logger: Logger = LoggerFactory.getLogger(EditMovie::class.java)

    /**
     * 動画作成開始
     * @param narrationPath ナレーションパス(最終的には削除する)
     * @param commentInfoDtoList コメント情報DTOリスト
     * @param outputDir 出力フォルダパス(最終的に出力するフォルダパス)
     */
    fun startCreate(
        narrationPath: String,
        commentInfoDtoList: List<CommentInfoDto>,
        outputDir: String,
        voiceFilePath: String,
        videoInfoDto: VideoInfoDto,
        videoId: String,
        userId: String,
        editMovieInfoDto: EditMovieInfoDto?,
        putImagePath: String,
    ) {
        logExecution(ConstantList.METHOD_STARTCREATE) {
            // ライブラリ読み込み
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

            Files.createDirectory(Path(outputDir))

            val photoPath = pathToString(
                putImagePath,
                ConstantList.FILE_ADD_PHOTOS.format("0")
            )
            val thumbnailPath = pathToString(outputDir, "Thumbnail.jpeg")
            createThumbnail(photoPath, thumbnailPath, videoInfoDto.title, outputDir)

            if(editMovieInfoDto == null){
                createVideoSamuneInfoRepository.insert(
                    CreateVideoSamuneInfoEntity(
                        userId = userId,
                        videoId = videoId,
                        title = videoInfoDto.title,
                        imageData = strPathToFileByte(outputDir, "Thumbnail.jpeg")
                    )
                )
            }else{
                createVideoSamuneInfoRepository.update(
                    CreateVideoSamuneInfoEntity(
                        userId = userId,
                        videoId = editMovieInfoDto.videoId,
                        title = videoInfoDto.title,
                        imageData = strPathToFileByte(outputDir, "Thumbnail.jpeg")
                    )
                )
            }


            try {
                addAudioToVideo(
                    narrationPath,
                    commentInfoDtoList,
                    outputDir,
                    voiceFilePath,
                    editMovieInfoDto,
                    putImagePath,
                    videoId,
                    userId,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if(editMovieInfoDto == null){
                createVideosRepository.insert(
                    CreateVideosEntity(
                        userId = userId,
                        videoId = videoId,
                        videoData = strPathToFileByte(outputDir, ConstantList.FILE_FINAL_VIDEO),
                        mobileVideoData = strPathToFileByte(
                            outputDir,
                            ConstantList.FILE_MOBILE_FINAL_VIDEO
                        )
                    )
                )
            } else {
                createVideosRepository.update(
                    CreateVideosEntity(
                        userId = editMovieInfoDto.userId,
                        videoId = editMovieInfoDto.videoId,
                        videoData = strPathToFileByte(outputDir, ConstantList.FILE_FINAL_VIDEO),
                        mobileVideoData = strPathToFileByte(
                            outputDir,
                            ConstantList.FILE_MOBILE_FINAL_VIDEO
                        )
                    )
                )
            }


        }
    }

    private fun addAudioToVideo(
        narrationPath: String,
        commentInfoDtoList: List<CommentInfoDto>,
        outputDir: String,
        voiceFilePath: String,
        editMovieInfoDto: EditMovieInfoDto?,
        putImagePath: String,
        videoId: String,
        userId: String,
    ) {
        logExecution(ConstantList.METHOD_ADDAUDIOTOVIDEO) {
            // ナレーションの長さを取得
            val narrationLength = getNarrationLength(voiceFilePath)
                ?: throw IllegalStateException("Failed to determine narration length.")

            // 背景動画パス取得
            val bmsPath = "$itemsPath${ConstantList.ITEMS_BACK_MOVIE}"
            val outputVideoPath = if (editMovieInfoDto == null) {
                getItemPath(bmsPath)
            } else {
                val bmsVideo = editMovieInfoDto.bmsName.split(".")
                pathToString(bmsPath, bmsVideo[0] + ".mp4")
            }

            // 背景動画とBGMの長さをナレーションに合わせて調整
            val extendedVideoPath = pathToString(narrationPath, ConstantList.FILE_EXTEND_VIDEO)
            extendVideo(
                FfmpegItemsDto(
                    inputs = listOf(outputVideoPath),
                    output = extendedVideoPath,
                    logFile = strToFile(outputDir, ConstantList.LOG_EXTEND_VIDEO),
                    narrationLength = narrationLength
                )
            )

            // BGMパス取得
            val musicPath = "$itemsPath${ConstantList.ITEMS_BACK_MUSIC}"
            val bgmPath = if (editMovieInfoDto == null) {
                getItemPath(musicPath)
            } else {
                pathToString(musicPath, editMovieInfoDto.bgmName)
            }

            val extendedBgmPath = pathToString(narrationPath, ConstantList.FILE_EXTEND_BGM)
            extendBGM(
                FfmpegItemsDto(
                    inputs = listOf(bgmPath),
                    output = extendedBgmPath,
                    logFile = strToFile(outputDir, ConstantList.LOG_EXTEND_BGM),
                    narrationLength = narrationLength
                )
            )

            val addPhotosVideo = pathToString(
                narrationPath,
                ConstantList.FILE_ADD_PHOTOS_VIDEO
            )
            val photoPath = pathToString(
                putImagePath,
                ConstantList.FILE_ADD_PHOTOS
            )
            // 写真表示時間取得
            val editImageNames = List(commentInfoDtoList.size) { index ->
                ConstantList.FILE_ADD_PHOTOS.format(index / 3)
            }
            val createImageFullPath: MutableList<String> = mutableListOf()
            val mainPhotoTimeDtoList =
                setPhotoTime(commentInfoDtoList, editMovieInfoDto, createImageFullPath, putImagePath)
            addMainPhoto(
                FfmpegItemsDto(
                    inputs = listOf(extendedVideoPath, photoPath),
                    output = addPhotosVideo,
                    logFile = strToFile(outputDir, ConstantList.LOG_ADD_PHOTO),
                    narrationLength = 0.0
                ), mainPhotoTimeDtoList, createImageFullPath
            )

            // コメントフレーム（テロップ）パス取得
            val cmtFramePath = "$itemsPath${ConstantList.ITEMS_COMMENT_FRAME}"
            val framePath = if (editMovieInfoDto == null) {
                getItemPath(cmtFramePath)
            } else {
                pathToString(cmtFramePath, editMovieInfoDto.cmtFrameName)
            }

            // コメントフレーム追加
            val addFrameMoviePath = pathToString(narrationPath, ConstantList.FILE_ADD_FRAME)
            addCommentFrame(
                FfmpegItemsDto(
                    inputs = listOf(addPhotosVideo, framePath),
                    output = addFrameMoviePath,
                    logFile = strToFile(outputDir, ConstantList.LOG_COMMENT_FRAME),
                    narrationLength = narrationLength
                )
            )

            // コメント表示
            val commentList: MutableList<List<String>> = mutableListOf()
            val basePath = pathToString(narrationPath, ConstantList.FILE_SET_TXT)
            setTextToVideo(
                FfmpegItemsDto(
                    inputs = listOf(addFrameMoviePath),
                    output = basePath,
                    logFile = strToFile(outputDir, ConstantList.LOG_COMMENT_TXT),
                    narrationLength = 0.0
                ),
                commentInfoDtoList,
                VideoType.PC.value,
                narrationPath,
                editMovieInfoDto?.sceneItems,
                commentList
            )

            // 最終的な動画を作成
            val outputFile = pathToString(outputDir, ConstantList.FILE_FINAL_VIDEO)
            val extendedBGMPath = Paths.get(extendedBgmPath).toString()
            val fileInputPaths = ConstantList.FFMPEG_INPUT_PATH.format(basePath) +
                    ConstantList.FFMPEG_INPUT_PATH.format(extendedBGMPath) +
                    ConstantList.FFMPEG_INPUT_PATH.format(strToAbsolutePath(voiceFilePath))

            val finalCommand = ConstantList.FFMPEG_BASE_CMD.format(
                fileInputPaths +
                        ConstantList.FFMPEG_FILTER_COMPLEX +
                        " \"[2:a]volume=3.0[a0];" +
                        " [1:a][a0]amix=inputs=2:duration=shortest[a]\"" +
                        ConstantList.FFMPEG_MAP.format("0:v") +
                        ConstantList.FFMPEG_MAP.format("[a]") +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.MEDIUM.value) +
                        ConstantList.FFMPEG_AUDIO_ENCODE.format(AudioCodicType.AAC.value) +
                        ConstantList.FFMPEG_STANDARDS_COMPLIANT.format(StandardsCompliantType.ESPERMENTAL.value) +
                        ConstantList.FFMPEG_SHORTEST +
                        ConstantList.STR_HARF_SPACE + outputFile
            )

            // プロセス実行
            processExe(
                finalCommand,
                File(pathToString(outputDir, ConstantList.LOG_FINAL))
            )

            // モバイル用の動画作成
            createMobileVideo(
                narrationLength,
                commentInfoDtoList,
                mainPhotoTimeDtoList,
                outputDir,
                narrationPath,
                extendedBGMPath,
                voiceFilePath,
                photoPath,
                editMovieInfoDto
            )

            if(editMovieInfoDto == null){
                val bmsSplit = outputVideoPath.split(ConstantList.STR_EN_MARK)
                val cmtSplit = framePath.split(ConstantList.STR_EN_MARK)
                val bgmSplit = bgmPath.split(ConstantList.STR_EN_MARK)
                val bmsImage = bmsSplit[bmsSplit.lastIndex].split(".")
                editVideoInfoRepository.insert(
                    EditVideoInfoEntity(
                        userId = userId,
                        videoId = videoId,
                        bmsName = bmsImage[0]+".png",
                        cmtFrameName = cmtSplit[cmtSplit.lastIndex],
                        bgmName = bgmSplit[bgmSplit.lastIndex],
                        comments = jacksonObjectMapper().writeValueAsString(commentList),
                        mainImages = jacksonObjectMapper().writeValueAsString(editImageNames),
                    )
                )
            }else{
                editVideoInfoRepository.update(
                    EditVideoInfoEntity(
                        userId = userId,
                        videoId = videoId,
                        bmsName = editMovieInfoDto.bmsName,
                        cmtFrameName = editMovieInfoDto.cmtFrameName,
                        bgmName = editMovieInfoDto.bgmName,
                        comments = jacksonObjectMapper().writeValueAsString(editMovieInfoDto.sceneItems.map{it.comment}),
                        mainImages = jacksonObjectMapper().writeValueAsString(editMovieInfoDto.sceneItems.map{it.mainImageName}),
                    )
                )
            }
        }
    }

    private fun getNarrationLength(voiceFilePath: String): Double? {
        return logExecution(ConstantList.METHOD_GETNARRATIONLENGTH) {
            val ffmpegCommand = arrayOf("ffmpeg", "-i", voiceFilePath, "-hide_banner")
            val process = ProcessBuilder(*ffmpegCommand).redirectErrorStream(true).start()
            var narrationLength: Double? = null

            process.inputStream.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    if (line.contains(ConstantList.STR_DURATION)) {
                        val duration = line.substringAfter(ConstantList.STR_DURATION)
                            .substringBefore(ConstantList.STR_COMMA)
                        val parts = duration.split(ConstantList.STR_COLON)
                        narrationLength =
                            parts[0].toDouble() * 3600 + parts[1].toDouble() * 60 + parts[2].toDouble()
                        return@forEach // ループからの早期退出
                    }
                }
            }
            narrationLength
        }
    }

    /**
     * ナレーションの長さに合わせて背景画像を作成（ループ処理）
     * @param ffmpegItems ffmpegのコマンドアイテム
     */
    private fun extendVideo(ffmpegItems: FfmpegItemsDto) {
        logExecution(ConstantList.METHOD_EXTENDVIDEO) {
            val capture = VideoCapture(ffmpegItems.inputs[0])
            val frameRate = capture.get(CAP_PROP_FPS)
            val totalFrames = capture.get(CAP_PROP_FRAME_COUNT)
            val videoLength = totalFrames / frameRate

            val loopCount = ceil(ffmpegItems.narrationLength / videoLength).toInt()
            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[0])

            // FFmpegを使って動画をループさせる
            val ffmpegCommand = ConstantList.FFMPEG_BASE_CMD.format(
                " -stream_loop ${loopCount - 1} $inputCmd -c copy ${ffmpegItems.output}"
            )
            // FFmpeg実行
            processExe(ffmpegCommand, ffmpegItems.logFile)
        }
    }

    /**
     * ナレーションの長さに合わせてBGMを作成
     * @param ffmpegItems ffmpegのコマンドアイテム
     */
    private fun extendBGM(ffmpegItems: FfmpegItemsDto) {
        logExecution(ConstantList.METHOD_EXTENDBGM) {
            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[0])
            val ffmpegCommand = ConstantList.FFMPEG_BASE_CMD.format(
                inputCmd + ConstantList.FFMPEG_FILTER_COMPLEX + " [0:a]volume=0.5,aresample=44100,apad=pad_dur=${ffmpegItems.narrationLength}[a]" +
                        ConstantList.FFMPEG_MAP.format("[a]") + " ${ffmpegItems.output}"
            )
            // ffmpeg実行
            processExe(ffmpegCommand, ffmpegItems.logFile)
        }
    }

    /**
     * コメントフレーム追加処理
     * @param ffmpegItems ffmpegのコマンドアイテム
     */
    private fun addCommentFrame(ffmpegItems: FfmpegItemsDto) {
        logExecution(ConstantList.METHOD_ADDCOMMENTFRAME) {
            val fileInputPaths =
                ffmpegItems.inputs.joinToString(separator = ConstantList.STR_BLANK) {
                    ConstantList.FFMPEG_INPUT_PATH.format(it)
                }
            val ffmpegCmd = ConstantList.FFMPEG_BASE_CMD.format(
                fileInputPaths +
                        ConstantList.FFMPEG_FILTER_COMPLEX +
                        " \"[1:v]format=rgba,colorchannelmixer=aa=1.0[img];" +
                        " [0:v][img]overlay=0:'(H-h)':enable='between(t,0,${ffmpegItems.narrationLength})';\"" +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.FAST.value) +
                        " -an ${ffmpegItems.output}"
            )
            // プロセス実行
            processExe(ffmpegCmd, ffmpegItems.logFile)
        }
    }

    private fun setPhotoTime(
        commentInfoDtoList: List<CommentInfoDto>,
        editMovieInfoDto: EditMovieInfoDto?,
        createImageNames: MutableList<String>,
        putImagePath: String,
    ): MutableList<MainPhotoTimeDto> {
        return logExecution(ConstantList.METHOD_SETPHOTOTIME) {
            var photoStartTime = 0.toFloat()
            var photoEndTime = 0.toFloat()

            val mainPhotoTimeDtoList: MutableList<MainPhotoTimeDto> = mutableListOf()
            if (editMovieInfoDto == null) {
                commentInfoDtoList.forEachIndexed { index, data ->
                    if (index % 3 == 0) {
                        // 画像表示時間追加
                        if (index != 0) {
                            if (index == commentInfoDtoList.lastIndex) {
                                // 最後の場合は画像表示時間追加
                                photoEndTime += data.time
                                mainPhotoTimeDtoList.add(
                                    MainPhotoTimeDto(
                                        photoStartTime = photoStartTime,
                                        photoEndTime = photoEndTime,
                                        totalPlayTime = photoEndTime - photoStartTime
                                    )
                                )
                            } else {
                                mainPhotoTimeDtoList.add(
                                    MainPhotoTimeDto(
                                        photoStartTime = photoStartTime,
                                        photoEndTime = photoEndTime,
                                        totalPlayTime = photoEndTime - photoStartTime
                                    )
                                )
                                // 初期化
                                photoStartTime = photoEndTime
                                photoEndTime += data.time
                            }
                        } else {
                            //　画像表示時間を追加
                            photoEndTime += data.time
                        }
                    } else if (index == commentInfoDtoList.lastIndex) {
                        //　画像表示時間を追加
                        photoEndTime += data.time
                        // 画像表示時間追加
                        mainPhotoTimeDtoList.add(
                            MainPhotoTimeDto(
                                photoStartTime = photoStartTime,
                                photoEndTime = photoEndTime,
                                totalPlayTime = photoEndTime - photoStartTime
                            )
                        )
                    } else {
                        //　画像表示時間を追加
                        photoEndTime += data.time
                    }
                }
            } else {
                var beforeImageName = editMovieInfoDto.sceneItems[0].mainImageName
                createImageNames.add(pathToString(putImagePath,beforeImageName))
                commentInfoDtoList.forEachIndexed { index, data ->
                    if (beforeImageName == editMovieInfoDto.sceneItems[index].mainImageName) {
                        if (index == commentInfoDtoList.lastIndex) {
                            // 最後の場合は画像表示時間追加
                            photoEndTime += data.time
                            mainPhotoTimeDtoList.add(
                                MainPhotoTimeDto(
                                    photoStartTime = photoStartTime,
                                    photoEndTime = photoEndTime,
                                    totalPlayTime = photoEndTime - photoStartTime
                                )
                            )
                        } else {
                            photoEndTime += data.time
                        }
                    } else {
                        beforeImageName = editMovieInfoDto.sceneItems[index].mainImageName
                        createImageNames.add(pathToString(putImagePath,beforeImageName))
                        mainPhotoTimeDtoList.add(
                            MainPhotoTimeDto(
                                photoStartTime = photoStartTime,
                                photoEndTime = photoEndTime,
                                totalPlayTime = photoEndTime - photoStartTime
                            )
                        )
                        // 初期化
                        photoStartTime = photoEndTime
                        photoEndTime += data.time
                    }
                }
            }
            mainPhotoTimeDtoList
        }
    }

    /**
     * 字幕設置
     * @param ffmpegItems ffmpegのコマンドアイテム
     * @param commentInfoList 字幕情報DTOリスト
     * @param videoType 作成するビデオタイプ
     * @param narrationPath ナレーションパス
     */
    fun setTextToVideo(
        ffmpegItems: FfmpegItemsDto,
        commentInfoList: List<CommentInfoDto>,
        videoType: String,
        narrationPath: String,
        editCommentList: List<EditMovieSeneInfoDto>?,
        commentList: MutableList<List<String>>
    ) {
        logExecution(ConstantList.METHOD_SETTEXTTOVIDEO) {
            var drawTextCommands = ConstantList.STR_BLANK
            var time = 0.toFloat()

            val drawCom =
                "drawtext=text='%s':font='%s':fontsize=%s:fontcolor=white:borderw=5:bordercolor=black:%s:enable='between(t,%s,%s)'"
            val font = "BIZ UDPゴシック"
            val tempPoint = "x=(w-text_w)/2:y=%s"
            commentInfoList.forEachIndexed { index, commentInfoDto ->
                val (setStartTime, setEndTime) = if (index == 0) {
                    Pair(0.toFloat(), commentInfoDto.time)
                } else {
                    Pair(time, time + commentInfoDto.time)
                }
                val text = commentInfoDto.text.replace("\r", "").replace("\n", "")

                val drawTextCommand = if (VideoType.PC.value == videoType) {
                    if(editCommentList == null){
                        // テキストを「、」で分割する
                        val textSplit = text.split("(?<=、)".toRegex()).toMutableList()
                        // テキストが２つより多い（２行より多い）場合、２行にするように調整する
                        while (textSplit.size > 2) {
                            if (canMerge(textSplit[0], textSplit[1])) {
                                textSplit[0] += textSplit.removeAt(1)
                            } else if (canMerge(textSplit[1], textSplit[2])) {
                                textSplit[1] += textSplit.removeAt(2)
                            } else {
                                val splitNum = textSplit[1].length / 2
                                textSplit[0] += textSplit[1].substring(0, splitNum)
                                textSplit[2] = textSplit[1].substring(splitNum) + textSplit[2]
                                textSplit.removeAt(1)
                            }
                        }
                        commentList.add(textSplit)
                        // テキストの位置調整
                        textSplit.mapIndexed { i, s ->
                            val point = if (textSplit.size == 1) {
                                tempPoint.format("600+(text_h/2)")
                            } else {
                                if (i == 0) {
                                    tempPoint.format("600")
                                } else {
                                    tempPoint.format("610+text_h")
                                }
                            }
                            drawCom.format(s, font, "36", point, setStartTime, setEndTime)
                        }.joinToString(separator = ", ")
                    } else {
                         editCommentList[index].comment.mapIndexed { i, s ->
                            val point = if (editCommentList[index].comment.size == 1) {
                                tempPoint.format("600+(text_h/2)")
                            } else {
                                if (i == 0) {
                                    tempPoint.format("600")
                                } else {
                                    tempPoint.format("610+text_h")
                                }
                            }
                            drawCom.format(s, font, "36", point, setStartTime, setEndTime)
                        }.joinToString(separator = ", ")
                    }
                } else {
                    val fontSize = 65
                    val point = tempPoint.format("1000")

                    drawCom.format(text, font, fontSize, point, setStartTime, setEndTime)
                }

                drawTextCommands += drawTextCommand

                if (index != commentInfoList.lastIndex) {
                    drawTextCommands += ConstantList.STR_COMMA + ConstantList.STR_HARF_SPACE
                }
                time = setEndTime
            }

            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[0])
            val ffmpegCommand: String
            val ffmpegMainCmd =
                "$inputCmd %s -c:v libx264 -crf 23 -preset fast -an ${ffmpegItems.output}"

            if (drawTextCommands.length > 8000) {
                val path = stringToPath(narrationPath, ConstantList.FILE_MOBILE_DRAW_CMD)
                Files.write(path, drawTextCommands.toByteArray(StandardCharsets.UTF_8))
                ffmpegCommand =
                    ConstantList.FFMPEG_BASE_CMD.format(ffmpegMainCmd.format("-filter_complex_script $path"))
            } else {
                ffmpegCommand =
                    ConstantList.FFMPEG_BASE_CMD.format(ffmpegMainCmd.format("-vf \"$drawTextCommands\""))
            }
            try {
                // ffmpgetの実行
                processExe(ffmpegCommand, ffmpegItems.logFile)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * 画像設置
     * @param ffmpegItems ffmpegのコマンドアイテム
     * @param photoInfoList 設置用画像情報リスト
     */
    fun addMainPhoto(ffmpegItems: FfmpegItemsDto, photoInfoList: List<MainPhotoTimeDto>, createImageNames: MutableList<String>) {
        logExecution(ConstantList.METHOD_ADDMAINPHOTO) {
            // ====== 画像を移動処理　START =======

            var inputs = ConstantList.STR_BLANK
            var formats = ConstantList.STR_BLANK
            var overlays = ConstantList.STR_BLANK

            val moveTime = 0.7
            photoInfoList.forEachIndexed { index, mainPhotoTimeDto ->
                inputs += ConstantList.FFMPEG_INPUT_PATH.format(
                    if(createImageNames.isEmpty()){
                        ffmpegItems.inputs[1].format(index)
                            .replace(ConstantList.STR_EN_MARK, ConstantList.STR_SLASH)
                    }else{
                        createImageNames[index].replace(ConstantList.STR_EN_MARK, ConstantList.STR_SLASH)
                    }
                )
                formats += "[${index + 1}:v]scale=-1:460,pad=iw+10:ih+10:5:5:color=black,format=rgba,colorchannelmixer=aa=1.0[img${index + 1}];"
                val baseWaitTime = mainPhotoTimeDto.photoEndTime - moveTime
                val moveCommand = "x='" + // x座標
                        // t <= %s（t=時間、%s=画面中央に移動するまでの時間）の場合（ffmpegのifはエクセルのIF関数と同じ考え）
                        "if(lte(t,%s),-w + (main_w/2 + w/2)*(%s/$moveTime)," +
                        // ↑の%s < t <= ↓の%s （t=時間、↑の%s=画面中央に移動するまでの時間、↓の%s=画面中央に滞在する時間）の場合
                        "if(lte(t,${mainPhotoTimeDto.photoEndTime - moveTime}),main_w/2 - w/2," +
                        // ↑の%s < t <= photoEndTime（t=時間、↑の%s=画面中央に滞在する時間、photoEndTime=画面外に移動するまでの時間＆描画されている時間）
                        "if(lte(t,${mainPhotoTimeDto.photoEndTime}),main_w/2 - w/2 + (main_w/2 + w/2)*((t-$baseWaitTime)/$moveTime),main_w)))':" +
                        "y=((H-250)-h)/2:" + // y座標は上部からフレームの中央に配置
                        // 描画時間
                        "enable='between(t,%s,${mainPhotoTimeDto.photoEndTime})'"
                val secondOverMoveTime = mainPhotoTimeDto.photoStartTime - moveTime
                val secondOverCommand = "[bg$index][img${index + 1}]overlay=${
                    moveCommand.format(
                        mainPhotoTimeDto.photoStartTime,
                        "(t-$secondOverMoveTime)",
                        secondOverMoveTime
                    )
                }%s;"

                overlays += when (index) {
                    0 -> {
                        "[0:v][img1]overlay=${moveCommand.format(moveTime, "t", 0)}[bg1];"
                    }

                    photoInfoList.lastIndex -> {
                        secondOverCommand.format(ConstantList.STR_BLANK)
                    }

                    else -> {
                        secondOverCommand.format("[bg${index + 1}]")
                    }
                }
            }

            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[0])

            val ffmpegCommand = ConstantList.FFMPEG_BASE_CMD.format(
                "$inputCmd$inputs " +
                        ConstantList.FFMPEG_FILTER_COMPLEX +
                        " \"$formats $overlays\"" +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.FAST) +
                        " -an ${ffmpegItems.output}"
            )
            // ffmpegの実行
            processExe(ffmpegCommand, ffmpegItems.logFile)
            // ====== 画像を移動処理　END =======
        }
    }

    /**
     * モバイル用動画作成
     *
     */
    private fun createMobileVideo(
        narrationLength: Double,
        commentInfoDtoList: List<CommentInfoDto>,
        photoInfoList: List<MainPhotoTimeDto>,
        outputDir: String,
        narrationPath: String,
        extendedBgmPath: String,
        voiceFilePath: String,
        photoPath: String,
        editMovieInfoDto: EditMovieInfoDto?
    ) {
        logExecution(ConstantList.METHOD_CREATEMOBILEVIDEO) {
            // モバイルサイズの動画を作成
            val mobileDefaultVideoPath = pathToString(
                narrationPath,
                ConstantList.FILE_MOBILE_DEFAULT_VIDEO
            )
            createMobileSizeVideo(
                FfmpegItemsDto(
                    inputs = listOf(ConstantList.STR_BLANK),
                    output = mobileDefaultVideoPath,
                    logFile = strToFile(outputDir, ConstantList.LOG_MOBILE_DEFAULT),
                    narrationLength = narrationLength
                )
            )
            // 音声ファイル作成
            val mobileCommentInfoDto =
                voiceVoxClient.createMobileAudio(commentInfoDtoList)
            // 画像設置
            val mobilPhoto = pathToString(narrationPath, ConstantList.FILE_MOBILE_PHOTO)
            mobilePhotoSet(
                FfmpegItemsDto(
                    inputs = listOf(mobileDefaultVideoPath, photoPath),
                    output = mobilPhoto,
                    logFile = strToFile(outputDir, ConstantList.LOG_MOBILE_PHOTO),
                    narrationLength = narrationLength
                ),
                photoInfoList
            )

            val mobileTxtPath = pathToString(narrationPath, ConstantList.FILE_MOBILE_TXT)
            // 字幕設置
            setTextToVideo(
                FfmpegItemsDto(
                    inputs = listOf(mobilPhoto),
                    output = mobileTxtPath,
                    logFile = strToFile(outputDir, ConstantList.LOG_COMMENT_TXT),
                    narrationLength = 0.0
                ),
                mobileCommentInfoDto,
                VideoType.MOBILE.value,
                narrationPath,
                editMovieInfoDto?.sceneItems,
                mutableListOf()
            )

            lastMobileVideo(
                FfmpegItemsDto(
                    inputs = listOf(mobileTxtPath, extendedBgmPath, voiceFilePath),
                    output = pathToString(outputDir, ConstantList.FILE_MOBILE_FINAL_VIDEO),
                    logFile = strToFile(outputDir, ConstantList.LOG_MOBILE_FINAL),
                    narrationLength = 0.0
                )
            )
        }
    }

    /**
     * モバイルサイズのデフォルト動画作成
     * @param ffmpegItems ffmpegのコマンドアイテム
     */
    private fun createMobileSizeVideo(ffmpegItems: FfmpegItemsDto) {
        logExecution(ConstantList.METHOD_CREATEMOBILESIZEVIDEO) {

            val ffmpegCmd = ConstantList.FFMPEG_BASE_CMD.format(
                "-f lavfi -i color=c=blue:s=720x1280:d=${ffmpegItems.narrationLength}" +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.FAST) +
                        " ${ffmpegItems.output}"
            )
            // ffmpegの実行
            processExe(ffmpegCmd, ffmpegItems.logFile)
        }
    }

    /**
     * モバイル用に画像を設定
     * @param ffmpegItems ffmpegのコマンドアイテム
     * @param photoInfoList 写真情報リスト
     */
    private fun mobilePhotoSet(ffmpegItems: FfmpegItemsDto, photoInfoList: List<MainPhotoTimeDto>) {
        logExecution(ConstantList.METHOD_MOBILEPHOTOSET) {

            val mobilePhotoCmdSetDto = photoInfoList.mapIndexed { index, it ->
                val inputCmd =
                    ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[1].format(index))

                val indexPlusOne = index + 1
                val imageSize = getImageSize(ffmpegItems.inputs[1].format(index))
                val magnification = (1280.toDouble() / (imageSize?.second ?: 1280).toDouble())
                val sizeCmd =
                    "[$indexPlusOne:v]scale=${(imageSize?.first ?: 1280) * magnification}:${(imageSize?.second ?: 720) * magnification}[img$indexPlusOne]"

                val overlayCmd = if (index == 0) {
                    "[0:v][img1]overlay=x='if(lte(t,${it.photoEndTime}),(main_w-w)*(t /${it.photoEndTime}),w)':y=0:enable='between(t,0,${it.photoEndTime})'[tmp1]"
                } else {
                    "[tmp$index][img$indexPlusOne]overlay=x='if(lte(t,${it.photoEndTime}),(main_w-w)*((t-${it.photoStartTime}) /${it.photoEndTime - it.photoStartTime}),w)':y=0:enable='between(t,${it.photoStartTime}, ${it.photoEndTime})'[tmp$indexPlusOne]"
                }

                MobilePhotoCmdSetDto(inputCmd, sizeCmd, overlayCmd)
            }

            val inputFile =
                ConstantList.FFMPEG_INPUT_PATH.format(ffmpegItems.inputs[0]) +
                        mobilePhotoCmdSetDto.joinToString(separator = ConstantList.STR_BLANK) { it.inputPathCmd }
            val reSize =
                mobilePhotoCmdSetDto.joinToString(
                    separator = ConstantList.STR_SEMI_COLON + ConstantList.STR_HARF_SPACE,
                    postfix = ConstantList.STR_SEMI_COLON
                ) { it.sizeCmd }
            val overlay =
                mobilePhotoCmdSetDto.joinToString(separator = ConstantList.STR_SEMI_COLON + ConstantList.STR_HARF_SPACE) { it.overlayCmd }

            val ffmpegCmd = ConstantList.FFMPEG_BASE_CMD.format(
                inputFile +
                        ConstantList.FFMPEG_FILTER_COMPLEX + " \"$reSize $overlay\"" +
                        ConstantList.FFMPEG_MAP.format("[tmp${photoInfoList.lastIndex + 1}]") +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.FAST.value) + " ${ffmpegItems.output}"
            )
            // ffmpegの実行
            processExe(ffmpegCmd, ffmpegItems.logFile)
        }
    }

    /**
     * モバイル用動画作成最終行程
     * @param ffmpegItems ffmpegのコマンドアイテム
     */
    private fun lastMobileVideo(ffmpegItems: FfmpegItemsDto) {
        logExecution(ConstantList.METHOD_LASTMOBILEVIDEO) {
            // 最終的な動画を作成
            val fileInputPaths =
                ffmpegItems.inputs.joinToString(separator = ConstantList.STR_BLANK) {
                    ConstantList.FFMPEG_INPUT_PATH.format(it)
                }

            val finalCommand = ConstantList.FFMPEG_BASE_CMD.format(
                fileInputPaths +
                        ConstantList.FFMPEG_FILTER_COMPLEX +
                        " \"[2:a]volume=3.0[a0];" +
                        " [1:a][a0]amix=inputs=2:duration=shortest[a]\"" +
                        ConstantList.FFMPEG_MAP.format("0:v") +
                        ConstantList.FFMPEG_MAP.format("[a]") +
                        ConstantList.FFMPEG_VIDEO_ENCODE.format(VideoCodicType.H264_01.value) +
                        ConstantList.FFMPEG_CRF.format(ConstantList.FFMPEG_CRF_DEFAULT_VAL) +
                        ConstantList.FFMPEG_PRESET.format(PresetType.MEDIUM.value) +
                        ConstantList.FFMPEG_AUDIO_ENCODE.format(AudioCodicType.AAC.value) +
                        ConstantList.FFMPEG_STANDARDS_COMPLIANT.format(StandardsCompliantType.ESPERMENTAL.value) +
                        ConstantList.FFMPEG_SHORTEST +
                        ConstantList.STR_HARF_SPACE + ffmpegItems.output
            )

            // プロセス実行
            processExe(finalCommand, ffmpegItems.logFile)
        }
    }

    /**
     * 画像のサイズ取得
     * @param filePath ファイルパス
     * @return Pair<Int, Int> 画像の横・縦
     */
    private fun getImageSize(filePath: String): Pair<Int, Int>? {
        return logExecution(ConstantList.METHOD_GETIMAGESIZE) {
            try {
                val file = File(filePath)
                val image: BufferedImage = ImageIO.read(file)
                val width = image.width
                val height = image.height
                Pair(width, height)  // サイズをPairで返す
            } catch (e: Exception) {
                println("画像の読み込みに失敗しました: ${e.message}")
                null
            }
        }
    }

    /**
     * ffmpeg実行
     * @param ffmpegCmd ffmpegのコマンド
     * @param logFile 出力するログファイル名
     */
    fun processExe(ffmpegCmd: String, logFile: File) {
        logExecution("processExe") {
            logger.info(messageSource.getMessageSource(ConstantList.MSG_I_003, arrayOf(ffmpegCmd)))
            // 実行するプロセス用意(windows用)
            val processBuilder = ProcessBuilder(
                ConstantList.FFMPEG_EXE_CMD_WIN,
                ConstantList.FFMPEG_OPT_CMD_WIN,
                ffmpegCmd
            )
            processBuilder.redirectErrorStream(true)

            // プロセス実行
            val process = processBuilder.start()
            val output = StringBuilder()

            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    output.appendLine(line)
                }
            }

            val exitCode = process.waitFor()

            if (exitCode != 0) {
                logFile.writeText(output.toString())
                throw RuntimeException(
                    messageSource.getMessageSource(ConstantList.MSG_E_001, arrayOf())
                )
            } else {
                logger.info("$process:success")
            }
        }
    }

    /**
     * サイズ変更（動画）
     * @param inputPath リサイズ前動画パス
     * @param outputPath リサイズ後動画パス
     * @param resizeWidth リサイズ幅
     * @param resizeHeight リサイズ高さ
     */
    fun resizeMovie(
        inputPath: String,
        outputPath: String,
        resizeWidth: Int,
        resizeHeight: Int,
        outputDir: String
    ) {
        logExecution(ConstantList.METHOD_RESIZEMOVIE) {
            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(inputPath)
            val ffmpegCommand = ConstantList.FFMPEG_BASE_CMD.format(
                inputCmd + ConstantList.FFMPEG_SCALE_SIZE.format(resizeWidth, resizeHeight) +
                        ConstantList.FFMPEG_AUDIO_ENCODE.format(AudioCodicType.COPY.value) + " " +
                        outputPath
            )

            processExe(
                ffmpegCommand,
                File(pathToString(outputDir, ConstantList.LOG_RESIZE_MOVIE))
            )
        }
    }

    /**
     * サイズ変更（写真）
     * @param inputPath リサイズ前写真パス
     * @param outputPath リサイズ語写真パス
     * @param resizeWidth リサイズ幅
     * @param resizeHeight リサイズ高さ
     */
    fun resizePhoto(
        inputPath: String,
        outputPath: String,
        resizeWidth: Int,
        resizeHeight: Int,
        outputDir: String
    ) {
        logExecution(ConstantList.METHOD_RESIZEPHOTO) {
            val inputCmd = ConstantList.FFMPEG_INPUT_PATH.format(inputPath)
            val ffmpegCmd = ConstantList.FFMPEG_BASE_CMD.format(
                "$inputCmd -vf scale=$resizeWidth:$resizeHeight $outputPath"
            )

            processExe(
                ffmpegCmd,
                File(pathToString(outputDir, ConstantList.LOG_RESIZE_PHOTO))
            )
        }
    }

    fun createThumbnail(inputImagePath: String, outputThumbnailPath: String, videoTitle: String, outputDir: String) {
        logExecution(ConstantList.METHOD_CREATETHUMBNAIL) {
            val ffmpegCmd = ConstantList.FFMPEG_BASE_CMD.format(
                ConstantList.FFMPEG_INPUT_PATH.format(inputImagePath) +
                        " -vf \"scale=640:481,drawbox=x=0:y=381:w=iw:h=100:color=black@0.7:t=fill," +
                        "drawtext=text='${videoTitle}':font='BIZ UDPゴシック':fontcolor=white:fontsize=36:x=(w-text_w)/2:y=431\"" +
                        " $outputThumbnailPath"
            )

            processExe(ffmpegCmd, strToFile(outputDir, ConstantList.LOG_TETHUMBNAIL))
        }
    }
}