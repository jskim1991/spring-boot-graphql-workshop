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

@Controller
class GraphQLController(private val storyRepository: StoryRepository, private val commentRepository: CommentRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @QueryMapping
    fun stories(): List<Story> {
        val stories = storyRepository.findAll().toList()
        return stories
    }

    /* V1 */
//    @SchemaMapping(typeName = "Story", field = "comments")
//    fun comments(story: Story): List<Comment> {
//        logger.info("fetching comments for story: ${story.id}")
//        return commentRepository.findAllByStoryIdIn(listOf(story.id))
//    }

    /* V2 */
//    @BatchMapping(typeName = "Story", field = "comments")
//    fun comments(stories: List<Story>): Map<Story, List<Comment>> {
//        logger.info("fetching comments for stories: ${stories.map { it.id }}")
//
//        val comments = commentRepository.findAllByStoryIdIn(stories.map { it.id })
//            .groupBy { it.storyId }
//
//        return stories.associateWith { comments[it.id] ?: emptyList() }
//    }

    /* V3 */
    @SchemaMapping(typeName = "Story", field = "comments")
    fun comments(story: Story, commentsByStoryId: DataLoader<Long, List<Comment>>): CompletableFuture<List<Comment>> {
        logger.info("fetching comments for story: ${story.id}")
        return commentsByStoryId.load(story.id)
    }

    @MutationMapping
    fun createStory(@Argument input: CreateStoryRequest): Story {
        return storyRepository.save(Story(
            id = 0L,
            title = input.title,
            description = input.description
        ))
    }

    @MutationMapping
    fun createComment(@Argument input: CreateCommentRequest): Comment {
        return commentRepository.save(Comment(
            id = 0L,
            storyId = input.storyId,
            content = input.content,
        ))
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

@Configuration
class BatchLoaderConfig(
    private val batchLoaderRegistry: BatchLoaderRegistry,
    private val commentRepository: CommentRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun registerBatchLoaders() {
        batchLoaderRegistry.forName<Long, List<Comment>>("commentsByStoryId")
            .registerMappedBatchLoader { storyIds, _ ->
                Mono.fromCallable {
                    logger.info("Fetching comments from database story IDs: $storyIds")
                    val comments = commentRepository.findAllByStoryIdIn(storyIds.toList())
                        .groupBy { it.storyId }

                    val result = storyIds.associateWith { comments[it] ?: emptyList() }
                    result
                }.subscribeOn(Schedulers.boundedElastic())
            }
    }
}