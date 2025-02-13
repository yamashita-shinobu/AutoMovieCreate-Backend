package com.example.testgpttovox.service.items

import com.example.testgpttovox.dto.CommentInfoDto
import com.example.testgpttovox.enum.MediaType
import com.example.testgpttovox.enum.VoiceVoxType
import com.example.testgpttovox.service.BaseService
import com.example.testgpttovox.service.CreateMovieService
import com.example.testgpttovox.util.ConstantList
import com.example.testgpttovox.util.MsgSource
import com.example.testgpttovox.util.StringUtils
import com.example.testgpttovox.util.StringUtils.Companion.sudachiSplit
import com.example.testgpttovox.util.VoiceVoxUtils
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.*
import java.nio.file.Paths
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException
import java.nio.file.Files

@Service
class VoiceVoxClient(
    messageSource: MsgSource,
) : BaseService(messageSource) {

    private val logger: Logger = LoggerFactory.getLogger(CreateMovieService::class.java)

    /**
     * 音声リストを作成
     */
    fun createListAudio(
        textList: List<String>,
        workDir: String,
        filename: String,
        voiceChara: String,
        voiceType: String,
        editComment: List<List<String>>?
    ): MutableList<CommentInfoDto> {
        return logExecution("createListAudio") {
            val commentInfoDtoList: MutableList<CommentInfoDto> = mutableListOf()

            if (editComment == null) {
                textList.forEachIndexed { index, data ->
                    createAudio(
                        index = index,
                        data = data,
                        workDir = workDir,
                        filename = filename,
                        voiceChara = voiceChara,
                        voiceType = voiceType,
                        commentInfoDtoList = commentInfoDtoList
                    )
                }
            } else {
                editComment.forEachIndexed() { index, data ->
                    createAudio(
                        index = index,
                        data = data.joinToString(""),
                        workDir = workDir,
                        filename = filename,
                        voiceChara = voiceChara,
                        voiceType = voiceType,
                        commentInfoDtoList = commentInfoDtoList
                    )
                }
            }
            commentInfoDtoList
        }
    }

    /**
     * テキストから音声データに変換
     */
    fun textToSpeech(text: String, voiceChara: String, voiceType: String): ByteArray? {
        // ナレーション設定
        val speaker = VoiceVoxType.toValue(VoiceVoxType.toCode(voiceChara))

        // 音声変換用情報取得
        val audioQueryResponse = VoiceVoxUtils.getVoiceData(
            VoiceVoxUtils.getVoiceAudioUrl(voiceType),// voiceVoxURLを取得
            text,
            speaker,
            ConstantList.STR_BLANK.toRequestBody()
        )

        val audioQueryJson =
            jacksonObjectMapper().readValue(audioQueryResponse.body?.string() ?: "",
                object : TypeReference<MutableMap<String, Any>>() {})

        audioQueryJson["speedScale"] = "1.3"
        Files.write(
            Paths.get("src/main/resources/testFile/test.txt"),
            jacksonObjectMapper().writeValueAsBytes(audioQueryJson)
        )

        // 音声データ取得
        val synthesisResponse =
            VoiceVoxUtils.getVoiceData(
                VoiceVoxUtils.getVoiceSynthesisUrl(voiceType),// voiceVoxSynthesisURLを取得
                text,
                speaker,
                jacksonObjectMapper().writeValueAsString(audioQueryJson)
                    .toRequestBody(MediaType.JSON.value.toMediaTypeOrNull()),
            )

        return synthesisResponse.body?.bytes()
    }

    /**
     * 音声ファイル再生時間取得
     * @param path 音声ファイルパス
     * @param text 音声ファイルテキスト
     * @return 音声情報DTO
     */
    fun getTimeVoice(path: String, text: String): CommentInfoDto {
        return logExecution("getTimeVoice") {
            val file = File(path)
            var time: Float
            try {
                // 自動でcloseするようにuseを使用
                AudioSystem.getAudioInputStream(file).use {
                    time = it.frameLength / it.format.frameRate
                }
            } catch (e: UnsupportedAudioFileException) {
                throw IllegalArgumentException("Unsupported audio file format: ${e.message}")
            }

            CommentInfoDto(
                path = path,
                text = text,
                time = time,
            )
        }
    }

    fun plusAudio(commentInfoDtoList: MutableList<CommentInfoDto>, outputFile: File) {
        logExecution("plusAudio") {

            // すべてのAudioInputStreamを取得し、InputStreamにキャストしてリスト化
            val audioInputStreams =
                commentInfoDtoList.map { AudioSystem.getAudioInputStream(File(it.path)) }

            try {
                // 合計フレーム数を計算
                val totalFrameLength = audioInputStreams.sumOf { it.frameLength }

                // すべてのInputStreamを連結
                val sequenceInputStream = audioInputStreams
                    .map { it as InputStream }
                    .reduce { acc, inputStream -> SequenceInputStream(acc, inputStream) }

                // 最初のファイルのフォーマットを取得
                val format = audioInputStreams[0].format

                // 結合されたInputStreamからAudioInputStreamを作成
                val outputStream =
                    AudioInputStream(sequenceInputStream, format, totalFrameLength)

                // 出力ファイルに書き込む
                AudioSystem.write(outputStream, AudioFileFormat.Type.WAVE, outputFile)

                println("Audio files successfully merged into ${outputFile.absolutePath}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                audioInputStreams.forEach {
                    try {
                        it.close()
                    } catch (e: Exception) {
                        println("削除失敗:$e")
                    }
                }
            }
        }
    }

    /**
     * モバイル用に表示するコメントテキストを生成
     * @param commentInfoDtoList PC用コメント情報一覧
     * @return List<CommentInfoDto> モバイル用コメント情報一覧
     */
    fun createMobileAudio(
        commentInfoDtoList: List<CommentInfoDto>,
    ): List<CommentInfoDto> {
        return logExecution("createMobileAudio") {
            val mobileCommentInfoDto: MutableList<CommentInfoDto> = mutableListOf()
            commentInfoDtoList.map {
                println("mobileText:"+it)
                mobileCommentInfoDto += splitIntoSegments(it.text, it.time)
            }

            mobileCommentInfoDto
        }
    }

    /**
     * モバイル用にテキストを分割し表示時間を計算する
     * @param text 分割前のコメント
     * @param totalTime 分割前のコメント音声の時間
     * @param maxLength モバイル用コメントの最大文字数
     * @return List<CommentInfoDit> 分割後のモバイル用コメント
     */
    fun splitIntoSegments(
        text: String,
        totalTime: Float,
        maxLength: Int = 10
    ): List<CommentInfoDto> {
        return logExecution("createMobileAudio") {
            val mobileCommentInfoDto: MutableList<CommentInfoDto> = mutableListOf()

            // sudachiで分割された文字は細分化されているため10文字程度になるように編集する
            var textString = ConstantList.STR_BLANK
            sudachiSplit(text)?.forEach {
                if (StringUtils.isPunctuation(it.surface())) {
                    // 文字が記号の文末で使用される記号の場合そこで文の結合を終了する
                    textString += it.surface()
                    mobileCommentInfoDto.add(addDto(textString, text.length, totalTime))
                    textString = ConstantList.STR_BLANK
                } else {
                    if ((textString + it.surface()).length <= maxLength) {
                        // 文を結合した時最大文字数を越えなかった場合文字結合
                        textString += it.surface()
                    } else {
                        // 超えた場合元の文字列を登録し新たに文字を上書きする
                        mobileCommentInfoDto.add(addDto(textString, text.length, totalTime))
                        textString = it.surface()
                    }
                }
            }

            mobileCommentInfoDto
        }
    }

    /**
     * コメント情報DTOにモバイルコメントを追加
     * @param str 分割後のコメント
     * @param textLength 分割前のコメント文字数
     * @param totalTime 分割前の音声時間
     * @return CommentInfoDto モバイル用コメント情報DTO
     */
    fun addDto(str: String, textLength: Int, totalTime: Float): CommentInfoDto {
        return CommentInfoDto(
            ConstantList.STR_BLANK,
            str,
            (totalTime * str.length / textLength)
        )
    }

    fun createAudio(
        index: Int,
        data: String,
        workDir: String,
        filename: String,
        voiceChara: String,
        voiceType: String,
        commentInfoDtoList: MutableList<CommentInfoDto>,
    ) {
        /*
            クロスプラットフォームの場合OS依存のパス対応(\と/を区別させないといけない)
            をしなければいけないので
            Paths.get()を使用したほうが良い：自動で対応してくれるため
            Path()では対応してくれない可能性があるらしい(chatGPT情報)
        */
        val filePath = Paths.get(workDir, filename.format(index))
        val audioData = textToSpeech(data, voiceChara, voiceType)
        if (audioData != null) {
            FileOutputStream(filePath.toString()).use { fos ->
                fos.write(audioData)
            }
            commentInfoDtoList.add(getTimeVoice(filePath.toString(), data))
        }
    }
}