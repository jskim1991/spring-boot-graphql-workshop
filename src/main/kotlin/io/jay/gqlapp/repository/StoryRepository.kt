package io.jay.gqlapp.repository

import io.jay.gqlapp.domain.Story
import org.springframework.data.repository.CrudRepository

interface StoryRepository : CrudRepository<Story, Long> {

}
