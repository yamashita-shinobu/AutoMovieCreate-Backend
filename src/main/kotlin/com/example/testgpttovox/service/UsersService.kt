package com.example.testgpttovox.service

import com.example.testgpttovox.dto.CreateVideoSettingDto
import com.example.testgpttovox.entity.CreateVideoSettingEntity
import com.example.testgpttovox.entity.UserInfoEntity
import com.example.testgpttovox.repository.CreateVideoSettingRepository
import com.example.testgpttovox.repository.UserInfoRepository
import com.example.testgpttovox.response.getUnsplashImage
import com.example.testgpttovox.response.translateText
import com.example.testgpttovox.util.*
import com.example.testgpttovox.util.StringUtils.Companion.getSudachiNoun
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersService(
    private val userInfoRepository: UserInfoRepository,
    private val createVideoSettingRepository: CreateVideoSettingRepository,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * ログイン時ユーザ登録
     */
    @Transactional
    fun login(userId: String, userName: String, email: String) {
        logExecution("UsersService_login") {
            if(!userInfoRepository.selectExists(userId = userId)){
                userInfoRepository.insert(
                    UserInfoEntity(
                        userId = userId,
                        userName = userName,
                        email = email,
                    )
                )
                createVideoSettingRepository.insert(
                    CreateVideoSettingEntity(
                        userId = userId,
                        chatGptVersion = "0",
                        voiceVoxType = "0",
                        voiceVoxNarration = "3"
                    )
                )
            }
        }
    }

}
