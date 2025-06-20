package io.jay.gqlapp.controller.rest

import io.jay.gqlapp.controller.dto.StoryResponse
import io.jay.gqlapp.domain.Comment
import io.jay.gqlapp.repository.CommentRepository
import io.jay.gqlapp.repository.StoryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stories")
class StoryController(
    private val storyRepository: StoryRepository,
    private val commentRepository: CommentRepository
) {

    @GetMapping
    fun stories(@RequestParam(required = false) fields: List<String>?): List<StoryResponse> {
        val stories =  storyRepository.findAll().toList()

        val comments = if (fields?.contains("comments") ?: false) {
            commentRepository.findAllByStoryIdIn(stories.map { it.id } )
                .groupBy { it.storyId }
        } else {
            emptyMap()
        }

        return stories.map { story ->
            StoryResponse.from(story, comments.getOrDefault(story.id, emptyList()))
        }
    }
}