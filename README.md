## Introduction to GraphQL

What is GraphQL?
- Query language for API developed by Meta
- Provides schema and types of the data in the API
- Supports query, mutation, and subscription
- Allows clients to request only the data they need

GraphQL Disadvantages
- Requires a library
- may not be worth it for simple CRUD operations
- difficult to cache due to `POST` requests

## Database Schema
```sql
CREATE TABLE IF NOT EXISTS stories (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    story_id INTEGER NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    content TEXT NOT NULL
);
```

## GraphQL schema
- Create a `schema.graphqls` file under `src/main/resources`
```graphql
type Query {
    stories: [Story]
}

type Mutation {
    createStory(input: CreateStoryRequest): Story!
    createComment(input: CreateCommentRequest): Comment!
}

type Story {
    id: ID!
    title: String!
    description: String!
    comments: [Comment]
    labels: [String]
}

type Comment {
    id: ID!
    storyId: ID!
    content: String!
}

input CreateStoryRequest {
    title: String!
    description: String!
}

input CreateCommentRequest {
    storyId: ID!
    content: String!
}
```

## Demo
- Rest API approach with selective fields
- QueryMapping
- SchemaMapping
- BatchMapping
- Batch Loader and Data loader
- Asynchronous Batch Loading
- MutationMapping

### Client Query and Mutation
```graphql
query stories {
  stories {
    description
    id
    title
    comments {
      id
      content
    }
    labels
  }
}

mutation createStory {
  createStory(input: {
    title: "CI/CD pipeline",
    description: ""
  }) {
    id
    title
    description
    comments {
      id
      content
    }
  }
}

mutation createComment {
  createComment(input:{
    storyId: 3,
    content: "Let's use Github Actions"
  }) {
    id
    storyId
    content
  }
}
```