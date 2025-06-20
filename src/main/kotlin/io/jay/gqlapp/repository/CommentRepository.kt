package io.jay.gqlapp.repository

import io.jay.gqlapp.domain.Comment
import org.springframework.data.repository.CrudRepository

interface CommentRepository : CrudRepository<Comment, Long> {
    fun findAllByStoryIdIn(storyIds: List<Long>): List<Comment>
}