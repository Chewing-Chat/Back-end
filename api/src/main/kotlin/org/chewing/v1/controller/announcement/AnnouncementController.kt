package org.chewing.v1.controller.announcement

import org.chewing.v1.dto.response.announcement.AnnouncementDetailResponse
import org.chewing.v1.dto.response.announcement.AnnouncementListResponse
import org.chewing.v1.service.announcement.AnnouncementService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/announcement")
class AnnouncementController(
    private val announcementService: AnnouncementService,
) {

    @GetMapping("/list")
    fun getAnnouncements(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<AnnouncementListResponse> {
        // 공지사항 목록을 반환하는 로직
        val announcements = announcementService.readAnnouncements()
        return ResponseHelper.success(AnnouncementListResponse.of(announcements))
    }

    @GetMapping("/{announcementId}")
    fun getAnnouncement(
        @RequestAttribute("userId") userId: String,
        @PathVariable announcementId: String,
    ): SuccessResponseEntity<AnnouncementDetailResponse> {
        // 공지사항을 반환하는 로직
        val announcement = announcementService.readAnnouncement(announcementId)
        return ResponseHelper.success(AnnouncementDetailResponse.of(announcement))
    }
}
