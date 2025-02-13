package com.example.testgpttovox.service

import com.example.testgpttovox.dto.AudioInsertInputDto
import com.example.testgpttovox.entity.AudiosEntity
import com.example.testgpttovox.repository.AudiosRepository
import com.example.testgpttovox.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path

/**
 *  音声・音楽ファイル登録サービスクラス
 *  @author 山下　忍
 */
@Service
class InsertAudioService(
    val audiosRepository: AudiosRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * 音声・音楽登録
     * @param audioInsertInputDto 音声ファイル情報DTO
     */
    @Transactional
    fun insert(audioInsertInputDto: AudioInsertInputDto) {
        logExecution("InsertAudioService_insert") {
            // ディレクトリに設置しているファイルを登録用に設定
            val insertFile = File(audioInsertInputDto.audioName)
            // 音声・音楽ファイルの中身を設定
            val audioData = if (audioInsertInputDto.audioData.isEmpty()) {
                if (Files.exists(Path(audioInsertInputDto.audioName))) {
                    // リクエストにはいってない場合はディレクトリから読み込む
                    insertFile.readBytes()
                } else {
                    // ファイルがない場合は例外発生
                    throw IOException("ファイルが存在しません")
                }
            } else {
                // リクエストに入っていたらそのまま設定
                audioInsertInputDto.audioData
            }
            // 音声・音楽ファイルの名称を設定
            val audioName = if (audioInsertInputDto.audioData.isEmpty()) {
                // ファイルを読み込んで名称を設定
                insertFile.name
            } else {
                // リクエストを設定
                audioInsertInputDto.audioName
            }

            // エンティティに設定
            val audiosEntity = AudiosEntity(
                audioName = audioName,
                audioData = audioData
            )

            audiosRepository.insert(audiosEntity)
        }
    }
}