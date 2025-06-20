package io.jay.gqlapp.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("STORIES")
data class Story(
    @Id val id: Long,
    val title: String,
    val description: String
)
