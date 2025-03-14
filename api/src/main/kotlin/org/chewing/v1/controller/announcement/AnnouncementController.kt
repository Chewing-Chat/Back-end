package org.chewing.v1.controller.announcement

import org.chewing.v1.dto.response.announcement.AnnouncementDetailResponse
import org.chewing.v1.dto.response.announcement.AnnouncementListResponse
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.announcement.AnnouncementService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/announcement")
class AnnouncementController(
    private val announcementService: AnnouncementService,
) {

    @GetMapping("/list")
    fun getAnnouncements(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<AnnouncementListResponse> {
        val announcements = announcementService.readAnnouncements()
        return ResponseHelper.success(AnnouncementListResponse.of(announcements))
    }

    @GetMapping("/{announcementId}")
    fun getAnnouncement(
        @CurrentUser userId: UserId,
        @PathVariable announcementId: String,
    ): SuccessResponseEntity<AnnouncementDetailResponse> {
        val announcement = announcementService.readAnnouncement(AnnouncementId.of(announcementId))
        return ResponseHelper.success(AnnouncementDetailResponse.of(announcement))
    }
}
