package org.chewing.v1.repository.jpa.emoticon

import org.chewing.v1.jparepository.emoticon.EmoticonJpaRepository
import org.chewing.v1.jparepository.emoticon.EmoticonPackJpaRepository
import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.chewing.v1.repository.emoticon.EmoticonRepository
import org.springframework.stereotype.Repository

@Repository
internal class EmoticonRepositoryImpl(
    private val emoticonJpaRepository: EmoticonJpaRepository,
    private val emoticonPackJpaRepository: EmoticonPackJpaRepository,
) : EmoticonRepository {
    override fun readEmoticonPacks(emoticonPackIds: List<String>): List<EmoticonPackInfo> = emoticonPackJpaRepository.findAllById(emoticonPackIds).map { it.toEmoticonPack() }

    override fun readEmoticons(emoticonPackIds: List<String>): List<EmoticonInfo> = emoticonJpaRepository.findAllByEmoticonPackIdIn(emoticonPackIds).map { it.toEmoticon() }
}
