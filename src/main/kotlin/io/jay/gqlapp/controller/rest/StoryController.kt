package io.jay.gqlapp.controller.rest

import io.jay.gqlapp.domain.Comment
import io.jay.gqlapp.domain.Story
import io.jay.gqlapp.repository.CommentRepository
import io.jay.gqlapp.repository.StoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stories")
class StoryController(private val storyRepository: StoryRepository, private val commentRepository: CommentRepository) {

    @GetMapping
    fun stories(@RequestParam(required = false) fields: List<String>?): List<StoryResponse> {
        val stories = storyRepository.findAll().toList()

        val commentsByStoryId = if (fields?.contains("comments") == true) {
            commentRepository.findAllByStoryIdIn(stories.map { it.id })
                .groupBy { it.storyId }
        } else {
            emptyMap()
        }

        return stories.map { story ->
            StoryResponse.from(story, commentsByStoryId[story.id] ?: emptyList())
        }
    }
}

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
                comments = comments.map { CommentResponse.from(it) }
            )
        }
    }
}

data class CommentResponse(
    val id: Long,
    val storyId: Long,
    val content: String
) {
    companion object {
        fun from(comment: Comment): CommentResponse {
            return CommentResponse(
                id = comment.id,
                storyId = comment.storyId,
                content = comment.content
            )
        }
    }
}