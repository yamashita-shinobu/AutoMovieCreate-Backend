package com.example.testgpttovox.util

import com.example.testgpttovox.enum.VoiceVoxType
import org.springframework.stereotype.Component
import java.util.*

@Component
class ConstantList {
    companion object {
        /**
         * ========================
         * ファイル関連
         * ========================
         */
        // extendVideo
        const val FILE_EXTEND_VIDEO = "extended_video.mp4"

        // extendBGM
        const val FILE_EXTEND_BGM = "extended_bgm.wav"

        // voice
        const val FILE_VOICE = "voice.wav"

        // addFrame
        const val FILE_ADD_FRAME = "addFrame.mp4"

        // setText
        const val FILE_SET_TXT = "setTextVideo.mp4"

        // addPhotosVideo
        const val FILE_ADD_PHOTOS_VIDEO = "addPhotosVideo.mp4"

        // finalVideo
        const val FILE_FINAL_VIDEO = "finalVideo.mp4"

        // ビデオに設置する画像のファイル名
        const val FILE_ADD_PHOTOS = "photo_%s.jpg"

        // mobileDefaultVideo
        const val FILE_MOBILE_DEFAULT_VIDEO = "mobileDefaultVideo.mp4"

        // mobileTxt
        const val FILE_MOBILE_TXT = "mobile_txt.mp4"

        // mobilePhoto
        const val FILE_MOBILE_PHOTO = "mobile_photo.mp4"

        // mobileFinalVideo
        const val FILE_MOBILE_FINAL_VIDEO = "mobile_final_video.mp4"

        // mobileDrawCmd
        const val FILE_MOBILE_DRAW_CMD = "mobileDrawCmd.txt"

        // resourceフォルダパス
        const val RESOURCE_PATH = "src/main/resources"

        // voicevoxで作成したファイル置き場
        const val DIR_VOICEVOX_NAME = "voiceVoxFiles"

        // 作成した動画置き場
        const val DIR_CREATE_VIDEO = "createVideo"

        // パス作成テンプレート
        const val PATH_TEMPLATE = "%s/%s"

        /* ffmpegログファイル */
        // finalLog
        const val LOG_FINAL = "ffmpeg_final_log.txt"

        // extendBGMLog
        const val LOG_EXTEND_BGM = "ffmpeg_extend_bgm_log.txt"

        // extendVideoLog
        const val LOG_EXTEND_VIDEO = "ffmpeg_extend_video_log.txt"

        // resizeMovieLog
        const val LOG_RESIZE_MOVIE = "ffmpeg_resize_movie_log.txt"

        // resizePhotoLog
        const val LOG_RESIZE_PHOTO = "ffmpeg_resize_photo_log.txt"

        // commentFrameLog
        const val LOG_COMMENT_FRAME = "ffmpeg_comment_frame_log.txt"

        // commentTxtLog
        const val LOG_COMMENT_TXT = "ffmpeg_comment_txt_log.txt"

        // addPhotoLog
        const val LOG_ADD_PHOTO = "ffmpeg_add_photo_log.txt"

        // mobileDefaultLog
        const val LOG_MOBILE_DEFAULT = "ffmpeg_mobile_default_log.txt"

        // mobilePhotoLog
        const val LOG_MOBILE_PHOTO = "ffmpeg_mobile_photo_log.txt"

        // mobileFinalLog
        const val LOG_MOBILE_FINAL = "ffmpeg_mobile_final_log.txt"

        // mobileFinalLog
        const val LOG_TETHUMBNAIL = "ffmpeg_Thumbnail_log.txt"

        /**
         * =====================================
         * message.properties
         * =====================================
         */
        // message_info_001
        const val MSG_I_001 = "message.info.001"

        // message_info_002
        const val MSG_I_002 = "message.info.002"

        // message_info_003
        const val MSG_I_003 = "message.info.003"

        // message_error_001
        const val MSG_E_001 = "message.error.001"

        /**
         * =====================================
         * methods
         * =====================================
         */
        // EditMove . startCreate
        const val METHOD_STARTCREATE = "startCreate"

        // EditMove . addAudioToVideo
        const val METHOD_ADDAUDIOTOVIDEO = "addAudioToVideo"

        // EditMove . extendBGM
        const val METHOD_EXTENDBGM = "extendBGM"

        // EditMove . extendVideo
        const val METHOD_EXTENDVIDEO = "extendVideo"

        // EditMove . resizeMovie
        const val METHOD_RESIZEMOVIE = "resizeMovie"

        // EditMove . resizePhoto
        const val METHOD_RESIZEPHOTO = "resizePhoto"

        // EditMove . addCommentFrame
        const val METHOD_ADDCOMMENTFRAME = "addCommentFrame"

        // EditMove . setTextToVideo
        const val METHOD_SETTEXTTOVIDEO = "setTextToVideo"

        // EditMove . addMainPhoto
        const val METHOD_ADDMAINPHOTO = "addMainPhoto"

        // EditMove . createMobileVideo
        const val METHOD_CREATEMOBILEVIDEO = "createMobileVideo"

        // EditMove . createMobileSizeVideo
        const val METHOD_CREATEMOBILESIZEVIDEO = "createMobileSizeVideo"

        // EditMove . mobilePhotoSet
        const val METHOD_MOBILEPHOTOSET = "mobilePhotoSet"

        // EditMove . lastMobileVideo
        const val METHOD_LASTMOBILEVIDEO = "lastMobileVideo"

        // EditMove . getImageSize
        const val METHOD_GETIMAGESIZE = "getImageSize"

        // EditMove . getNarrationLength
        const val METHOD_GETNARRATIONLENGTH = "getNarrationLength"

        // EditMove . setPhotoTime
        const val METHOD_SETPHOTOTIME = "setPhotoTime"

        // EditMove . createThumbnail
        const val METHOD_CREATETHUMBNAIL = "createThumbnail"

        /**
         * =====================================
         * ffmpegコマンド
         * =====================================
         */
        // ベースコマンド
        const val FFMPEG_BASE_CMD = "ffmpeg -y %s"

        // 入力パスコマンド
        const val FFMPEG_INPUT_PATH = " -i %s"

        // -filter_complex 複数のフィルター連結・入出力を処理・高度なフィルタリングを制御する
        const val FFMPEG_FILTER_COMPLEX = " -filter_complex"

        // mapコマンド
        const val FFMPEG_MAP = " -map %s"

        // 画面サイズ 引数(0)は縦、引数(1)は横
        const val FFMPEG_SCALE_SIZE = " -vf scale=%s:%s"

        // 映像のエンコーダ　引数にはVideoCodicTypeを指定
        const val FFMPEG_VIDEO_ENCODE = " -c:v %s"

        // 品質とファイルサイズのバランス 引数は0～51で指定する(値が小さいほど高品質。デフォルトは23）
        const val FFMPEG_CRF = " -crf %S"

        // 品質のデフォルト値(後々ENUMに移行したい）
        const val FFMPEG_CRF_DEFAULT_VAL = " 23"

        // エンコード速度と圧縮効率の設定　引数はPresetTypeを指定
        const val FFMPEG_PRESET = " -preset %s"

        // 音声のエンコーダ　引数にはAudioCodicTypeを指定
        const val FFMPEG_AUDIO_ENCODE = " -c:a %s"

        // 標準準拠モード設定
        const val FFMPEG_STANDARDS_COMPLIANT = " -strict %s"

        // 最短ストリームに合わせる
        const val FFMPEG_SHORTEST = " -shortest"

        // ffmpeg実行コマンド①
        const val FFMPEG_EXE_CMD_WIN = "cmd"

        // ffmpeg実行コマンド② Windows
        const val FFMPEG_OPT_CMD_WIN = "/c"

        // ffmpeg実行コマンド３ Linux
        const val FFMPEG_EXE_CMD_LIN = "bash"

        // ffmpeg実行コマンド④ Linux
        const val FFMPEG_OPT_CMD_LIN = "-c"

        /**
         * =====================================
         * 素材フォルダ
         * =====================================
         */
        // PC用動画の背景動画フォルダ
        const val ITEMS_BACK_MOVIE = "backMovie"

        // BGMフォルダ
        const val ITEMS_BACK_MUSIC = "bgm"

        // 動画関連画像フォルダ
        const val ITEMS_MAIN_PHOTO = "photo"

        // コメント表示用フレームフォルダ
        const val ITEMS_COMMENT_FRAME = "commentFrame"

        /**
         * =====================================
         * voiceVox関連
         * =====================================
         */
        // クエリテンプレート(引数 第1：URL 第2：テキスト 第3：speaker(語り部) )
        const val VOICEVOX_QUERY_TEMP = "%s?text=%s&speaker=%s"

        // 音量設定
        const val VOICEVOX_VOLUME_SET = "\"volumeScale\":%s"

        // 音速設定
        const val VOICEVOX_SPEED_SET = "\"speedScale\":%s"

        /**
         * =====================================
         * sudachi関連
         * =====================================
         */
        // 設定ファイルパス
        const val SUDACHI_SETTING_PATH = "src/main/resources/sudachi/sudachi.json"

        // 設置パス
        const val SUDACHI_DIR_PATH = "src/main/resources/sudachi/"

        /**
         * =====================================
         *　文字関連
         * =====================================
         */
        // ブランク
        const val STR_BLANK = ""

        // スラッシュ
        const val STR_SLASH = "/"

        // カンマ
        const val STR_COMMA = ","

        // コロン
        const val STR_COLON = ":"

        // セミコロン
        const val STR_SEMI_COLON = ";"

        // 半角空白
        const val STR_HARF_SPACE = " "

        // Duration
        const val STR_DURATION = "Duration: "

        // 左大かっこ
        const val STR_LEFT_SQUARE_BRACKET = "["

        // 右大かっこ
        const val STR_RIGHT_SQUARE_BRACKET = "]"

        // 円マーク
        const val STR_EN_MARK = "\\"

        // ドット
        const val STR_DOT = "."

        /**
         * =====================================
         *　文章関連
         * =====================================
         */
        // 説明文(第1：タイトル、第2：説明文、第3：タグ、第4クレジット)
        const val TXT_DESCRIPTION = """
                    生物の驚くべき成長や行動についてご紹介します。
                    今回は「%s」です。
                    %s
                    
                    %s
                    
                    %s
        """
        // vue.jsで表示するための画像変換
        const val VIEW_IMAGE = "data:%s;base64,%s"
    }
}