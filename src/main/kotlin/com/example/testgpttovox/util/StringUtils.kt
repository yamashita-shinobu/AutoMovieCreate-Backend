package com.example.testgpttovox.util

import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Morpheme
import com.worksap.nlp.sudachi.Tokenizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class StringUtils{
    companion object {

        /**
         *  特定の文字か判定
         */
        fun isPunctuation(text: String): Boolean {
            val punctuationList = listOf('、', '。', '！', '？')
            return text.any { it in punctuationList }
        }

        /**
         * 文字列に特定の文字がいくつあるか判定
         */
        fun checkPunctuationMarks(text: String): Pair<Boolean, Int> {
            // チェック対象の記号をリストにまとめる
            val marks = listOf('、', '。', '!', '?', '！', '？')

            // 含まれている記号の個数をカウント
            val count = text.count { it in marks }

            // 1つでも含まれていたら true とその個数を返す
            return Pair(count > 0, count)
        }

        /**
         * 長さが30以内で結合可能かチェックする
         */
        fun canMerge(first: String, second: String, limit: Int = 30): Boolean {
            return first.length + second.length <= limit
        }

        /**
         * 文字列をPathに変換
         */
        fun stringToPath(prefix: String, suffix: String): Path{
            return Paths.get(ConstantList.PATH_TEMPLATE.format(prefix, suffix))
        }

        /**
         * Pathを文字列に変換
         */
        fun pathToString(prefix: String, suffix: String): String{
            return stringToPath(prefix, suffix).toString()
        }

        /**
         * 文字列を絶対パスに変換
         */
        fun strToAbsolutePath(strPath: String): Path{
            return Paths.get(strPath).toAbsolutePath()
        }

        /**
         * 文字列のファイルパスをファイル型に変換
         */
        fun strToFile(prefix: String, suffix: String): File{
            return File(pathToString(prefix, suffix))
        }

        /**
         * 文字列のファイルパスのファイルを読み込みバイト配列に変換
         */
        fun strPathToFileByte(prefix: String, suffix: String): ByteArray{
            return strToFile(prefix, suffix).readBytes()
        }

        /**
         * 文字列をバイト配列（UTF-8）に変換
         */
        fun strToByteArrayAndUtf8(str: String): ByteArray{
            return str.toByteArray(StandardCharsets.UTF_8)
        }

        /**
         * スダチによる文字列の分割
         */
        fun sudachiSplit(text: String): MutableList<Morpheme>? {
            // 分割ツールsudachiの設定ファイル読み込み
            val sudachiSettingJsonPath =
                Paths.get(ConstantList.SUDACHI_SETTING_PATH).toAbsolutePath()
            val sudachiSettingJson = Files.readString(sudachiSettingJsonPath)
            // 分割ツールsudachiを実行しテキストを分割
            val sudachiDictionaryDirectory =
                Paths.get(ConstantList.SUDACHI_DIR_PATH).toAbsolutePath().toString()
            val sudachiDictionary =
                DictionaryFactory().create(sudachiDictionaryDirectory, sudachiSettingJson)
            val tokenizer = sudachiDictionary.create()

            return tokenizer.tokenize(Tokenizer.SplitMode.C, text)
        }

        /**
         * スダチで分割した文字列の名詞のみ取得
         */
        fun getSudachiNoun(text: String):List<String>?{
            val tokens = sudachiSplit(text)
                return tokens?.filter {it.partOfSpeech().contains("名詞")}
                    ?.map{it.surface()}
        }

        /**
         * アイテムパス取得
         */
        fun getItemPath(dir: String): String {
            // ファイルリスト取得
            val itemList = File(dir).list() ?: throw FileNotFoundException("アイテムが存在しません")
            // bgm取得
            val itemRandomNum = itemList.indices.shuffled().first()
            return pathToString(dir, itemList[itemRandomNum])
        }

        /**
         * vue.js画像表示用文字列変換
         */
        fun viewImageStr(fileType: String, file: File): String {
            return ConstantList.VIEW_IMAGE.format(
                fileType,
                Base64.getEncoder().encodeToString(file.readBytes())
            )
        }

        /**
         * 拡張子取得
         */
        fun getExtension(fileName: String): String{
            return fileName.substring(fileName.lastIndexOf(ConstantList.STR_DOT))
        }
    }
}
