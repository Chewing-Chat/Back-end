package org.chewing.v1.error

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

enum class ErrorCode(
    val code: String,
    val message: String,
) {
    // Auth errors
    WRONG_VALIDATE_CODE("AUTH_1", "인증 번호가 틀렸습니다."),
    EXPIRED_VALIDATE_CODE("AUTH_2", "인증 번호가 만료되었습니다."),
    TOKEN_EXPIRED("AUTH_3", "토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH_4", "토큰을 확인해주세요"),
    PHONE_NUMBER_IS_USED("AUTH_5", "해당 전화번호로 이미 다른 사람이 사용중입니다."),
    EMAIL_ADDRESS_IS_USED("AUTH_6", "해당 이메일로 이미 다른 사람이 사용중입니다."),
    EMAIL_NOT_FOUND("AUTH_7", "해당 이메일을 찾을 수 없습니다."),
    PHONE_NUMBER_NOT_FOUND("AUTH_8", "해당 전화번호를 찾을 수 없습니다."),

    // Common
    PATH_WRONG("COMMON_1", "잘못된 메세드입니다."),
    VARIABLE_WRONG("COMMON_2", "요청 변수가 잘못되었습니다."),
    WRONG_ACCESS("COMMON_3", "잘못된 접근입니다."),
    INTERNAL_SERVER_ERROR("COMMON_4", "Internal Server Error"),

    FILE_UPLOAD_FAILED("FILE_1", "파일 업로드를 실패하였습니다."),
    FILE_DELETE_FAILED("FILE_2", "파일 삭제를 실패하였습니다."),
    FILE_CONVERT_FAILED("FILE_3", "파일 변환에 실패하였습니다."),
    FILE_NAME_COULD_NOT_EMPTY("FILE_4", "파일 이름이 없습니다"),
    NOT_SUPPORT_FILE_TYPE("FILE_5", "지원하지 않는 형식의 파일입니다."),
    FILE_NAME_INCORRECT("FILE_6", "파일 이름이 잘못되었습니다."),

    USER_NOT_FOUND("USER_1", "회원을 찾을 수 없음."),
    USER_NOT_ACCESS("USER_2", "사용자가 활성화되지 않았습니다."),

    FRIEND_NOT_FOUND("FRIEND_1", "친구를 찾을 수 없음."),
    FRIEND_ALREADY_CREATED("FRIEND_2", "이미 추가된 친구입니다."),
    FRIEND_MYSELF("FRIEND_3", "자기 자신을 친구로 추가할 수 없습니다."),
    FRIEND_BLOCK("FRIEND_4", "차단한 친구입니다."),
    FRIEND_BLOCKED("FRIEND_5", "차단당한 친구입니다."),

    FEED_NOT_FOUND("FEED_1", "피드를 찾을 수 없음."),
    FEED_ALREADY_LIKED("FEED_2", "이미 공감한 피드입니다."),
    FEED_ALREADY_UNLIKED("FEED_3", "이미 공감을 취소한 피드입니다."),
    FEED_IS_NOT_OWNED("FEED_4", "피드 작성자가 아닙니다."),
    FEED_IS_OWNED("FEED_5", "피드 작성자입니다."),
    FEED_LIKED_FAILED("FEED_6", "피드 공감을 실패하였습니다."),
    FEED_UNLIKED_FAILED("FEED_7", "피드 공감 취소를 실패하였습니다."),

    FEED_COMMENT_IS_NOT_OWNED("COMMENT_1", "댓글 작성자가 아닙니다."),
    FEED_COMMENT_NOT_FOUND("COMMENT_2", "댓글을 찾을 수 없음."),
    FEED_COMMENT_FAILED("COMMENT_3", "댓글 작성을 실패하였습니다."),
    FEED_UNCOMMENT_FAILED("COMMENT_4", "댓글 삭제를 실패하였습니다."),

    EMOTICON_NOT_FOUND("EMOTICON_1", "이모티콘을 찾을 수 없음."),

    CHATROOM_NOT_FOUND("CHATROOM_1", "채팅방을 찾을 수 없음."),
    CHATROOM_CREATE_FAILED("CHATROOM_2", "채팅방 생성을 실패하였습니다."),
    CHATROOM_IS_NOT_GROUP("CHATROOM_3", "그룹 채팅방이 아닙니다."),
    CHATROOM_FAVORITE_FAILED("CHATROOM_4", "채팅방 즐겨찾기 설정을 실패하였습니다."),
    CHATLOG_NOT_FOUND("CHATLOG_1", "채팅 로그를 찾을 수 없음."),

    ANNOUNCEMENT_NOT_FOUND("ANNOUNCEMENT_1", "공지사항을 찾을 수 없음."),

    SCHEDULE_NOT_FOUND("SCHEDULE_1", "일정을 찾을 수 없음."),
    SCHEDULE_CREATE_FAILED("SCHEDULE_2", "일정 생성을 실패하였습니다."),

    AI_CREATE_FAILED("AI_1", "AI 생성을 실패하였습니다."),

    INVALID_TYPE("INVALID_1", "잘못된 타입입니다."),

    ;

    companion object {
        private val ERROR_CODE_MAP: Map<String, ErrorCode> = Stream.of(*entries.toTypedArray())
            .collect(Collectors.toMap(ErrorCode::message, Function.identity()))

        fun from(message: String?): ErrorCode? {
            if (ErrorCode.ERROR_CODE_MAP.containsKey(message)) {
                return ErrorCode.ERROR_CODE_MAP.get(message)
            }

            return ErrorCode.INTERNAL_SERVER_ERROR
        }
    }
}
