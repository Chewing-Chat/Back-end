package org.chewing.v1.controller.emoticon

import org.chewing.v1.dto.response.emoticon.EmoticonPacksResponse
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.emoticon.EmoticonService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/emoticon")
class EmoticonController(
    private val emoticonService: EmoticonService,
) {
    @GetMapping("/list")
    fun getEmoticonPacks(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<EmoticonPacksResponse> {
        val emoticonPacks = emoticonService.fetchUserEmoticonPacks(userId)
        return ResponseHelper.success(EmoticonPacksResponse.of(emoticonPacks))
    }
}
