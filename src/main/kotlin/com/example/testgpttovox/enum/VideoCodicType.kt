package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class VideoCodicType(
    override val code: String,
    override val value: String
): CodeValueEnum {
    H264_01("libx264", "libx264"), //  最も一般的に使用されるH.264エンコーダ。高品質で、幅広い互換性があります。
    // 例: ffmpeg -i input.mp4 -c:v libx264 output.mp4
    H264_02("h264_nvenc", "h264_nvenc"), // NVIDIAのGPUを使用したH.264エンコーダ。高速で、リアルタイムエンコーディングに適しています。
    // 例: ffmpeg -i input.mp4 -c:v h264_nvenc output.mp4
    H264_03("h264_qsv", "h264_qsv"), // Intel Quick Sync Videoを使用したH.264エンコーダ。Intel CPU内蔵のGPUを使用します。
    // 例: ffmpeg -i input.mp4 -c:v h264_qsv output.mp4
    H264_04("h264_amf", "h264_amf"), // AMD GPUを使用したH.264エンコーダ。
    // 例: ffmpeg -i input.mp4 -c:v h264_amf output.mp4
    H265_01("libx265", "libx265"), // H.265/HEVCエンコーダ。H.264よりも高い圧縮効率を提供しますが、エンコードには時間がかかります。
    // 例: ffmpeg -i input.mp4 -c:v libx265 output.mp4
    H265_02("hevc_nvenc", "hevc_nvenc"), // NVIDIA GPUを使用したH.265エンコーダ。
    // 例: ffmpeg -i input.mp4 -c:v hevc_nvenc output.mp4
    H265_03("hevc_qsv", "hevc_qsv"), // Intel Quick Sync Videoを使用したH.265エンコーダ。
    // 例: ffmpeg -i input.mp4 -c:v hevc_qsv output.mp4
    H265_04("hevc_amf", "hevc_amf"), // AMD GPUを使用したH.265エンコーダ。
    // 例: ffmpeg -i input.mp4 -c:v hevc_amf output.mp4
    MPEG4_01("mpeg4", "mpeg4"), // 古いMPEG-4 Part 2エンコーダ。互換性は高いが、圧縮効率はH.264より低いです。
    // 例: ffmpeg -i input.mp4 -c:v mpeg4 output.avi
    MPEG4_02("libxvid", "libxvid"), // Xvidエンコーダを使用したMPEG-4エンコード。一般的にAVIフォーマットで使用されます。
    // 例: ffmpeg -i input.mp4 -c:v libxvid output.avi
    VP8("libvpx", "libvpx"), // VP8エンコーダ。WebM形式で使用されることが多いです。
    // 例: ffmpeg -i input.mp4 -c:v libvpx output.webm
    VP9("libvpx-vp9", "libvpx-vp9"), // VP9エンコーダ。VP8よりも高い圧縮効率を提供します。
    // 例: ffmpeg -i input.mp4 -c:v libvpx-vp9 output.webm
    AV1("libaom-av1", "libaom-av1"), // AV1エンコーダ。非常に高い圧縮効率を提供しますが、エンコードには時間がかかります。
    // 例: ffmpeg -i input.mp4 -c:v libaom-av1 output.mkv
    SVT_AV1("libsvtav1", "libsvtav1"), // SVT-AV1エンコーダ。エンコード速度と効率をバランス良く提供します。
    // 例: ffmpeg -i input.mp4 -c:v libsvtav1 output.mkv
    PRORES("prores", "prores"), // Apple ProResエンコーダ。高品質のビデオ編集用に使用されます。
    // 例: ffmpeg -i input.mp4 -c:v prores output.mov
    DHXHD("dhxhd", "dhxhd"), // Avid DNxHDエンコーダ。高品質で、ポストプロダクションワークフローでよく使用されます。
    // 例: ffmpeg -i input.mp4 -c:v dnxhd output.mov
    DHXHR("dhxhr", "dhxhr"), // Avid DNxHRエンコーダ。4KやUHD解像度で使用されることが多いです。
    // 例: ffmpeg -i input.mp4 -c:v dnxhr output.mov
    RAWVIDEO("rawvideo", "rawvideo"), // 無圧縮のビデオエンコーダ。非常に大きなファイルサイズになりますが、品質は完全に維持されます。
    // 例: ffmpeg -i input.mp4 -c:v rawvideo output.avi
    FFVHUFF("ffvhuff", "ffvhuff"), // 可逆圧縮のFFV1エンコーダ。品質を保ちながらファイルサイズを削減できます。
    // 例: ffmpeg -i input.mp4 -c:v ffvhuff output.avi
    THEORA("rawvideo", "rawvideo"), // Theoraビデオエンコーダ。主にOggコンテナで使用されます。
    // 例: ffmpeg -i input.mp4 -c:v theora output.ogv
    LIBOPENH264("ffvhuff", "ffvhuff"); // CiscoのOpenH264エンコーダ。商業用途でロイヤリティフリーで使用できるH.264エンコーダです。
    // 例: ffmpeg -i input.mp4 -c:v libopenh264 output.mp4

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toValue<VideoCodicType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCode<VideoCodicType>(value)
        }
    }
}