package io.jay.gqlapp.controller.dto

data class CommentResponse(
    val id: Long,
    val content: String,
    val storyId: Long
)
