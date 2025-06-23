package io.jay.gqlapp.controller.graphql

import io.jay.gqlapp.domain.Comment
import io.jay.gqlapp.domain.Story
import io.jay.gqlapp.repository.CommentRepository
import io.jay.gqlapp.repository.StoryRepository
import jakarta.annotation.PostConstruct
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CompletableFuture

@Configuration
class GraphQLDataLoaderConfig(
    private val batchLoaderRegistry: BatchLoaderRegistry,
    private val commentRepository: CommentRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    private fun registerBatchLoaders() {
        batchLoaderRegistry.forName<Long, List<Comment>>("commentsByStoryId")
            .registerMappedBatchLoader { storyIds, _ ->
                Mono.fromCallable {
                    logger.info("data loader for commentsByStory called with storyIds: $storyIds")
                    val comments = commentRepository.findAllByStoryIdIn(storyIds.toList())
                    val result = comments.groupBy { it.storyId }
                    result
                }.subscribeOn(Schedulers.boundedElastic())
            }
    }
}

@Controller
class StoryGraphQLController(
    private val storyRepository: StoryRepository,
    private val commentRepository: CommentRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @QueryMapping
    fun stories(): List<Story> {
        logger.info("fetching all stories")
        return storyRepository.findAll().toList()
    }

    /* version 1 */
//    @SchemaMapping(typeName = "Story", field = "comments")
//    fun comments(story: Story): List<Comment> {
//        logger.info("fetching comments for story id: ${story.id}")
//        return commentRepository.findAllByStoryIdIn(listOf(story.id))
//    }

    /* version 2 */
//    @BatchMapping(typeName = "Story", field = "comments")
//    fun comments(stories: List<Story>): Map<Story, List<Comment>> {
//        logger.info("fetching comments for story ids: ${stories.map { it.id }}")
//
//        val storiesMap = stories.associateBy { it.id }
//
//        return commentRepository.findAllByStoryIdIn(stories.map { it.id })
//            .groupBy { storiesMap[it.storyId]!! }
//    }

    /* version 3 */
    @SchemaMapping(typeName = "Story", field = "comments")
    fun comments(story: Story, commentsByStoryId: DataLoader<Long, List<Comment>>): CompletableFuture<List<Comment>> {
        logger.info("fetching comments for story id: ${story.id}")
        return commentsByStoryId.load(story.id)
    }

    @MutationMapping
    fun createStory(@Argument input: CreateStoryRequest): Story {
        return storyRepository.save(
            Story(
                id = 0L,
                title = input.title,
                description = input.description
            )
        )
    }

    @MutationMapping
    fun createComment(@Argument input: CreateCommentRequest): Comment {
        return commentRepository.save(
            Comment(
                id = 0L,
                storyId = input.storyId,
                content = input.content
            )
        )
    }
}

data class CreateStoryRequest(
    val title: String,
    val description: String
)

data class CreateCommentRequest(
    val storyId: Long,
    val content: String
)