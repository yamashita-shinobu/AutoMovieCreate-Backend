package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class AudioCodicType(override val code: String, override val value: String): CodeValueEnum {
    AAC("aac", "aac"), // 非常に広く使われている音声コーデックで、特にMP4ファイルでよく使用されます。高い圧縮率と良好な音質のバランスを提供します。
    // 例: ffmpeg -i input.mp4 -c:a aac output.mp4
    LIBFDK_AAC("libfdk_aac", "libfdk_aac"), // FraunhoferによるAACエンコーダ。非常に高品質なAACエンコードが可能ですが、特許ライセンスが必要です。FFmpegのビルドに依存します。
    // 例: ffmpeg -i input.mp4 -c:a libfdk_aac output.mp4
    LIVMP3LAME("libmp3lame", "libmp3lame"), // MP3形式の音声をエンコードするためのエンコーダ。MP3はほとんどのデバイスやプラットフォームでサポートされており、互換性が高いです。
    // 例: ffmpeg -i input.mp4 -c:a libmp3lame output.mp3
    LIBOPUS("libopus", "libopus"), // 近年人気の高い音声コーデックで、特にリアルタイム通信（VoIP）やWebM形式での使用が推奨されます。低ビットレートでも高品質な音声を提供します。
    // 例: ffmpeg -i input.webm -c:a libopus output.webm
    LIBBORBIS("libvorbis", "libvorbis"), // Oggコンテナ形式で広く使用されているオーディオコーデックです。主にオープンソースコミュニティで支持されています。
    // 例: ffmpeg -i input.ogg -c:a libvorbis output.ogg
    PCM_SL6LE("pcm_sl6le","pcm_sl6le"), // リニアPCM（16ビット、リトルエンディアン）でエンコードされた無圧縮音声フォーマット。品質は非常に高いですが、ファイルサイズも大きくなります。
    // 例: ffmpeg -i input.wav -c:a pcm_s16le output.wav
    PCM_SL24LE("pcm_sl24le", "pcm_sl24le"), // 24ビットのリニアPCMでエンコードされた無圧縮音声。さらに高い品質を提供します。
    // 例: ffmpeg -i input.wav -c:a pcm_s24le output.wav
    FLAC("flac", "flac"), // 可逆圧縮形式で、品質を保ちながらファイルサイズを削減します。主に音楽ファイルで使用されます。
    // 例: ffmpeg -i input.wav -c:a flac output.flac
    AC3("ac3", "ac3"), // サラウンドサウンド用のエンコーダで、DVDやBlu-rayでよく使用されます。
    // 例: ffmpeg -i input.mp4 -c:a ac3 output.mkv
    EAC3("eac3", "eac3"), // エンハンストAC-3（Dolby Digital Plus）。AC3の進化版で、より高い圧縮効率を提供します。
    // 例: ffmpeg -i input.mp4 -c:a eac3 output.mkv
    WMAV2("wmav2", "wmay2"), // Windows Media Audio 2コーデック。主にWindows環境で使用されます。
    // 例: ffmpeg -i input.wav -c:a wmav2 output.wma
    LIBSPEEX("libspeex", "libspeex"), // 音声通信やVoIPで使用される低ビットレートの音声コーデック。特に音声通話での使用が想定されています。
    // 例: ffmpeg -i input.wav -c:a libspeex output.spx
    COPY("copy", "copy"); // 音声ストリームを再エンコードせずにコピーすることを指定します。エンコードなしで元のストリームをそのまま使用します。
    // 例: ffmpeg -i input.mp4 -c:a copy output.mp4

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<AudioCodicType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<AudioCodicType>(value)
        }
    }
}