package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jparepository.user.UserEmoticonJpaRepository
import org.chewing.v1.model.user.UserEmoticonPackInfo
import org.chewing.v1.repository.user.UserEmoticonRepository
import org.springframework.stereotype.Repository

@Repository
internal class UserEmoticonRepositoryImpl(
    private val userEmoticonJpaRepository: UserEmoticonJpaRepository,
) : UserEmoticonRepository {
    override fun readUserEmoticons(userId: String): List<UserEmoticonPackInfo> = userEmoticonJpaRepository.findAllByIdUserId(userId).map { it.toUserEmoticon() }
}
