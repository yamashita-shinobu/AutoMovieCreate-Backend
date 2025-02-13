package com.example.testgpttovox.util

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.VideoWriter
import org.opencv.videoio.Videoio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class TestCode {
    private val logger: Logger = LoggerFactory.getLogger(TestCode::class.java)

    fun overlayTextOnVideo(inputVideoPath: String, outputVideoPath: String, text: String) {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)

        val videoCapture = VideoCapture(inputVideoPath)

        if (!videoCapture.isOpened) {
            println("Error: Could not open video file.")
            return
        }

        val frameWidth = videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH).toInt()
        val frameHeight = videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT).toInt()
        val fps = videoCapture.get(Videoio.CAP_PROP_FPS)

        val fourcc = VideoWriter.fourcc('m', 'p', '4', 'v')
        val videoWriter = VideoWriter(outputVideoPath, fourcc, fps, Size(frameWidth.toDouble(), frameHeight.toDouble()), true)

        if (!videoWriter.isOpened) {
            println("Error: Could not open video writer.")
            return
        }

        val frame = Mat()

        while (true) {
            if (!videoCapture.read(frame)) {
                break
            }

            Imgproc.putText(frame, text, Point(50.0, 50.0), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, Scalar(255.0, 255.0, 255.0), 2)
            videoWriter.write(frame)
        }

        videoCapture.release()
        videoWriter.release()
        println("Video processing complete.")

        // ========== 以下はいろいろ試行錯誤結果 =================
//        val imagePath = "C:\\Users\\yama\\Downloads\\2744560_s.jpg"
//        val imagePath2 = "C:\\Users\\yama\\Downloads\\23955419_s.jpg"
//        val videoPath = "path/to/output_video.avi"
//        val outputPathWithAudio = "path/to/output_video_with_audio.mp4"
//        val audioPath = "path/to/your/audio.mp3"
//        val outputPath = "C:\\Users\\yama\\Downloads\\output_video_move.mp4"
//
//        val backgroundPath = "C:\\Users\\yama\\Downloads\\6394054-uhd_4096_2048_24fps.mp4"
//        val resizeBackgroundPath = "C:\\Users\\yama\\IdeaProjects\\testgpttovox\\extended_video.mp4"
//        val outputPath2 = "C:\\Users\\yama\\Downloads\\outputPath2.mp4"
//
//        val frameWidth = 1280
//        val frameHeight = 720
//
//        val frameCommentPath = "C:\\Users\\yama\\Downloads\\handwritten_loose_leaf_telop_white.png"
//        val reFrameCommentPath = "C:\\Users\\yama\\Downloads\\rePhoto.png"
//        val commentFrameWidth = 1280
//        val commentFrameHeight = 180
//
////        resizeMovie(backgroundPath, resizeBackgroundPath, frameWidth, frameHeight)
////        resizePhoto(frameCommentPath, reFrameCommentPath, commentFrameWidth, commentFrameHeight)
//
////        val durationInSeconds = 10
//        val fps = 30

        // 画像を読み込む
//        val image = imread(imagePath, IMREAD_UNCHANGED)
//        val image = imread(imagePath, IMREAD_COLOR)
//        val image2 = imread(reFrameCommentPath, IMREAD_COLOR)
//        val totalFrames = durationInSeconds * fps
//        val writer = VideoWriter(outputPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps.toDouble(), Size(frameWidth.toDouble(), frameHeight.toDouble()))
//
//        if (!writer.isOpened) {
//            println("Failed to open video writer")
//            return
//        }
//
//        for (i in 0 until totalFrames) {
//            // 透過フレームを作成
//            val frame = Mat(frameHeight, frameWidth, image.type(), Scalar.all(0.0))
//
//            // 画像のX座標を計算（画面外から左に入り、右に抜ける）
////            val x = ((i.toDouble() / totalFrames * (frameWidth + image.cols())) - image.cols()).toInt()
//            val x = if(i <= 10){
//                0
//            } else {
//                if(((i - 10) * (0.1 * image.cols()).toInt()) >= ((frameWidth - image.cols()) / 2)){
//                    // 画面中央になるまで移動する（画像表示と同じ速度）
//                    (frameWidth - image.cols()) / 2
//                }else{
//                    // 画像中央以上の移動になった場合は中央に固定表示する
//                    (i - 10) * (0.1 * image.cols()).toInt()
//                }
//            }
//            val y = (frameHeight - image.rows() -180) / 2
//
//            // 表示する幅を計算（徐々に表示される部分を広げる）
//            val visibleWidth = if(i <= 10){
//                // 10フレームまでは画像を表示させる
//                ((i.toDouble() / 10) * image.cols()).toInt()
//            } else {
//                image.cols()
//            }
//            // 画像がフレーム内に存在する場合のみ貼り付け
////            if (x + visibleWidth > 0 && x < frameWidth) {
//                // 画像の一部をコピーしてマスク処理
//                val subImage = image.colRange(image.cols() - visibleWidth, image.cols())
//                subImage.copyTo(frame.rowRange(y, y + image.rows()).colRange(maxOf(0, x), minOf(frameWidth, x + visibleWidth)))
////            }
//            image2.copyTo(frame.rowRange(frameHeight - image2.rows(),frameHeight).colRange(0,frameWidth))
//
//
//            // テキストを追加
//            val fontFace = FONT_HERSHEY_SIMPLEX
//            val fontScale = 5.0
//            val thickness = 2
//            val textPosition = Point(50.0, frameHeight - 50.0) // フレーム内のテキスト表示位置
//            val text = "皆さんご存知でしょうか？\nいま私はとても眠いです。"
//            putText(frame, text, textPosition, fontFace, fontScale, Scalar(255.0, 255.0, 255.0, 1.0), thickness, LINE_AA, false)
//            // フレームを書き込む
//            writer.write(frame)
//        }
//
//        writer.release()
//        println("Video created successfully at: $outputPath")



        // =========== 画像合成（移動させた画像の合成）ffmpeg使用 ============
//        val ffmpegCommand = arrayOf(
//            "ffmpeg", "-y", "-i", resizeBackgroundPath, "-i", outputPath,
////            "-filter_complex", "[1:v]colorkey=0x000000:0.1:0.1[fg];[0:v][fg]overlay[out]",
//            "-filter_complex", "[0:v][1:v]overlay",
//            "-map", "[out]", "-map", "0:a?", "-c:v", "libx264", "-c:a", "aac", "-strict", "experimental",
//            "-shortest", outputPath2
//        )

        //        val processBuilder = ProcessBuilder(
//            "ffmpeg", "-i", videoPath,
//            "-i", narrationPath,
//            "-i", bgmPath,
//            "-filter_complex", "[1:a][2:a]amix=inputs=2:duration=first[a]",
//            "-map", "0:v", "-map", "[a]",
//            "-c:v", "copy", "-c:a", "aac",
//            "-shortest",
//            outputVideoPath
//        )
//        processBuilder.redirectErrorStream(true)
//        val process = processBuilder.start()
//
//        val executor = Executors.newSingleThreadExecutor()
//        val future: Future<Int> = executor.submit<Int> {
//            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
//                var line: String?
//                while (reader.readLine().also { line = it } != null) {
//                    println(line)
//                }
//            }
//            process.waitFor()
//        }
//
//        try {
//            val exitCode = future.get(60, TimeUnit.SECONDS)
//            println("FFmpeg exited with code: $exitCode")
//        } catch (e: TimeoutException) {
//            process.destroy()
//            throw TimeoutException("FFmpeg process timed out and was terminated.")
//        } finally {
//            executor.shutdown()
//        }

        //        val ffmpegCommand = arrayOf(
//            "ffmpeg", "-y",
//            "-i", resizeBackgroundPath,
//            "-i", imagePath,
//            "-i", imagePath2,
//            "-i", imagePath3,
//            "-filter_complex",
//            "[1:v]format=rgba,colorchannelmixer=aa=1.0[img1];" +
//                    "[2:v]format=rgba,colorchannelmixer=aa=1.0[img2];" +
//                    "[3:v]format=rgba,colorchannelmixer=aa=1.0[img3];" +
//                    "[0:v][img1]overlay=x='if(lte(t,0.7),-w + (main_w/2 + w/2)*(t/0.7),if(lte(t,4.4),main_w/2 - w/2,if(lte(t,5.1),main_w/2 - w/2 + (main_w/2 + w/2)*((t-4.4)/0.7),main_w)))':y=0:enable='between(t,0,5.1)'[bg1];" +
//                    "[bg1][img2]overlay=x='if(lte(t,5.1),-w + (main_w/2 + w/2)*((t-4.4)/0.7),if(lte(t,7.8),main_w/2 - w/2,if(lte(t,8.5),main_w/2 - w/2 + (main_w/2 + w/2)*((t-7.8)/0.7),main_w)))':y=0:enable='between(t,4.4,8.5)'[bg2]" +
//                    "[bg2][img3]overlay=x='if(lte(t,5.1),-w + (main_w/2 + w/2)*((t-4.4)/0.7),if(lte(t,7.8),main_w/2 - w/2,if(lte(t,8.5),main_w/2 - w/2 + (main_w/2 + w/2)*((t-7.8)/0.7),main_w)))':y=0:enable='between(t,4.4,8.5)'",
//            "-c:v", "libx264",
//            "-crf", "23",
//            "-preset", "fast",
//            "-an",
//            outputPath2
//        )
        //        val processBuilder = ProcessBuilder(*ffmpegCommand)
//        val processBuilder = ProcessBuilder("bash", "-c", ffmpegCommand) // Linux/Macの場合
//        val processBuilder = ProcessBuilder("cmd", "/c", ffmpegCommand) // Windowsの場合

        //=============================================================
        logger.info("startCreate: End")
    }
//    fun addCommentText(videoPath: String, commentText: String): String{
//        val outputPath = "C:\\Users\\yama\\Downloads\\addText.mp4"
//        val ffmpegCommand = arrayOf(
//            "ffmpeg", "-y",
//            "-i", videoPath,
//            "-vf", "drawtext=text='$commentText':font='Meiryo UI':fontsize=24:fontcolor=white:borderw=2:bordercolor=black:x=100:y=600:enable='between(t,0,11)'",
//            "-c:v", "libx264",
//            "-crf", "23",
//            "-preset", "fast",
//            "-an",
//            outputPath
//        )
//
//        val processBuilder = ProcessBuilder(*ffmpegCommand)
//        processBuilder.redirectErrorStream(true)
//
//        val logFile = File("ffmpeg_combine_text_log.txt")
//        processBuilder.redirectOutput(logFile)
//
//        val process = processBuilder.start()
//
//        val reader = process.inputStream.bufferedReader()
//        reader.forEachLine { logger.info(it) }
//
//        val exitCode = process.waitFor()
//
//        if (exitCode == 0) {
//            logger.info("Video text completed successfully!")
//        } else {
//            logger.warn("Video text failed with exit code $exitCode")
//        }
//        return outputPath
//    }

//    processBuilder.redirectErrorStream(true)
//    // ログファイルに出力を記録
//    val logFile = File("ffmpeg_async_log.txt")
//    processBuilder.redirectOutput(logFile)
//
//    val process2 = processBuilder.start()
//
//    val executor = Executors.newSingleThreadExecutor()
//    val future: Future<Int> = executor.submit<Int> {
//            BufferedReader(InputStreamReader(process2.inputStream)).use { reader ->
//                var line: String?
//                while (reader.readLine().also { line = it } != null) {
//                    println(line)
//                }
//            }
//        process2.waitFor()
//    }
//
//    try {
//        val exitCode = future.get(600, TimeUnit.SECONDS)
//        println("FFmpeg exited with code: $exitCode")
//    } catch (e: TimeoutException) {
//        process.destroy()
//        throw TimeoutException("FFmpeg process timed out and was terminated.")
//    } finally {
//        executor.shutdown()
//    }
}