package io.jay.gqlapp.controller.dto

import io.jay.gqlapp.domain.Comment
import io.jay.gqlapp.domain.Story

data class StoryResponse(
    val id: Long,
    val title: String,
    val description: String,
    val comments: List<CommentResponse>
) {
    companion object {
        fun from(story: Story, comments: List<Comment>): StoryResponse {
            return StoryResponse(
                id = story.id,
                title = story.title,
                description = story.description,
                comments = comments.map {
                    CommentResponse(
                        id = it.id,
                        content = it.content,
                        storyId = it.storyId
                    )
                }
            )
        }
    }
}


