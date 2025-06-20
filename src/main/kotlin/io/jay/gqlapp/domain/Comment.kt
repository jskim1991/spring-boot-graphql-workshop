package io.jay.gqlapp.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("COMMENTS")
data class Comment(
    @Id val id: Long,
    val content: String,
    val storyId: Long
)