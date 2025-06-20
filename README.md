Introduction to GraphQL

What is GraphQL?
- Query language for API developed by Meta
- Provides schema and types of the data in the API
- Supports query, mutation, and subscription
- Allows clients to request only the data they need

GraphQL vs REST

GraphQL Disadvantages
- Requires a library
- may not be worth it for simple CRUD operations
- difficult to cache due to `POST` requests

Definining a GraphQL schema
- Create a `schema.graphqls` file under `src/main/resources`
```graphql
type Query {
    stories: [Story]
}

type Story {
    id: ID!
    title: String!
    description: String!
    comments: [Comment]
}

type Comment {
    id: ID!
    storyId: ID!
    content: String!
}
```


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