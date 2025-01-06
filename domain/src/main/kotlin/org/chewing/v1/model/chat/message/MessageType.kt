package org.chewing.v1.model.chat.message

enum class MessageType {
    REPLY,
    DELETE,
    LEAVE,
    READ,
    NORMAL,
    FILE,
    INVITE,
    BOMB,
}
// 답글 채팅(REPLY)
// 삭제된 채팅(DELETE)
// 단체 채팅방일 경우 친구가 나갔음을 알리는 채팅(LEAVE)
// 단체 채팅방일 경우 친구가 들어옴을 알리는 채팅(INVITE)
// 채팅방에 들어옴을 알리는 채팅(ENTER) -> 읽음 처리를 해야함
// 기본적인 텍스트 형태의 채팅(NORMAL)
// 파일 형태의 채팅(FILE)
// 앱에 들어옴(IN)
// 앱에서 나감(OUT)
