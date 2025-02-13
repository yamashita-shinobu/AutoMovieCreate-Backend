package com.example.testgpttovox.enum

import com.example.testgpttovox.util.CodeValueCreditEnum
import com.example.testgpttovox.util.CodeValueEnum
import com.example.testgpttovox.util.EnumHelper

enum class VoiceVoxType(override val code: String, override val value: String, override val credit: String):
    CodeValueCreditEnum {
    SIKOKU_METAN("四国めたん","2", "VOICEVOX:四国めたん"),
    ZUNDAMON("ずんだもん", "3", "VOICEVOX:ずんだもん"),
    KASUKABE_TUMUGI("春日部つむぎ","8", "VOICEVOX:春日部つむぎ"),
    NAMIOTO_RITU("波音りつ", "9", "VOICEVOX:波音りつ"),
    AMEHARU_HAU("雨晴はう", "10", "VOICEVOX:雨晴はう"),
    GENNO_TAKEHIRO("玄野武宏", "11", "VOICEVOX:玄野武宏"),
    HAKUJOU_KOTAROU("白上虎太郎", "12", "VOICEVOX:白上虎太郎"),
    AOYAMA_RYUSEI("青山龍星", "13", "VOICEVOX:青山龍星"),
    MEIMEI_HIMARI("冥鳴ひまり", "14", "VOICEVOX:冥鳴ひまり"),
    KYUSYU_SORA("九州そら", "16", "VOICEVOX:九州そら"),
    MOTIKOSAN("もち子さん", "20", "VOICEVOX:もち子さん"),
    KENZAKI_SIYUU("剣崎雌雄", "21", "VOICEVOX:剣崎雌雄"),
    WHITE_CUL("whiteCUL", "23", "VOICEVOX:whiteCUL"),
    GOKI("後鬼", "27", "VOICEVOX:後鬼"),
    NO7("No.7", "29","VOICEVOX:No.7"),
    TIBI_JI("ちび式じい", "42","VOICEVOX:ちび式じい"),
    OUKA_MIKO("櫻歌ミコ", "43","VOICEVOX:櫻歌ミコ"),
    SAYO("小夜/SAYO", "46","VOICEVOX:小夜/SAYO"),
    NASU_ROBO("ナースロボ＿タイプT", "47","VOICEVOX:ナースロボ＿タイプT"),
    SEIKISI("†聖騎士 紅桜†", "51","VOICEVOX:†聖騎士 紅桜†"),
    JANMATU_AKAJI("雀松朱司", "52","VOICEVOX:雀松朱司"),
    KIKAJUMA_SOURIN("麒ヶ島宗麟", "53","VOICEVOX:麒ヶ島宗麟"),
    HARUKA_NANA("春歌ナナ", "54","VOICEVOX:春歌ナナ"),
    NEKOSI_ARU("猫使アル", "55","VOICEVOX:猫使アル"),
    NEKOSI_BILI("猫使ビィ", "58","VOICEVOX:猫使ビィ"),
    CHUGOKU_USAGI("中国うさぎ", "61","VOICEVOX:中国うさぎ"),
    KURITA_MARON("栗田まろん", "67","VOICEVOX:栗田まろん"),
    AIERUTAN("あいえるたん", "68", "VOICEVOX:あいえるたん"),
    MANBETU_HANAMARU("満別花丸", "69", "VOICEVOX:満別花丸"),
    KINEI_NIA("琴詠ニア", "74","VOICEVOX:琴詠ニア"),
    ONNAGOE_1("女声1", "10005","VOICEVOX Nemo"),
    ONNAGOE_2("女声2", "10007", "VOICEVOX Nemo"),
    ONNAGOE_3("女声3", "10004", "VOICEVOX Nemo"),
    ONNAGOE_4("女声4", "10003", "VOICEVOX Nemo"),
    ONNAGOE_5("女声5", "10008", "VOICEVOX Nemo"),
    ONNAGOE_6("女声6", "10006", "VOICEVOX Nemo"),
    OTOKOGOE_1("男声1", "10001", "VOICEVOX Nemo"),
    OTOKOGOE_2("男声2", "10000", "VOICEVOX Nemo"),
    OTOKOGOE_3("男声3", "10002", "VOICEVOX Nemo");

    companion object {
        fun toValue(code: String): String {
            return EnumHelper.toVal<VoiceVoxType>(code)
        }

        fun toCode(value: String): String{
            return EnumHelper.toCd<VoiceVoxType>(value)
        }

        fun toCredit(code: String): String {
            return EnumHelper.toCredit<VoiceVoxType>(code)
        }
    }
}