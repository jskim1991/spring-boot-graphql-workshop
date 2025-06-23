## Introduction to GraphQL

### What is GraphQL?
- Query language for API developed by Meta
- Provides schema and types of the data in the API
- Supports query (read), mutation (write), and subscription (real-time updates)
- Allows clients to request only the data they need

### Similarities with REST
- Both are used for client-server communication over HTTP
- Both make a request via URL and return JSON

### Differences from REST
| Aspect            | REST                                                                                           | GraphQL                                                                                                                  |
|-------------------|:-----------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------|
| Data Fetching     | Resource-centric (/users, /products) and multiple endpoints per resource (/users, /users/{id}) | Single endpoint for querying all data                                                                                    |
| Over-fetching     | Must fetch full payloads even if only a few fields are needed (entire payload)                 | Avoided - clients request exactly what they need                                                                         |
| Under-fetching    | Multiple endpoints need to be aggregated or composite endpoint is needed                       | Avoided - clients request exactly what they need and GraphQL resolvers allow the backend to automatically aggregate data |
| Typed Schema      | No built-in schema definition - requires documentation                                         | Schema is strongly typed and validation support is provided                                                              |
| Real-time Support | Requires manual setup of WebSocket                                                             | Built-in subscription support for real-time data                                                                         |



## Demo
### Table of Contents
- Rest API approach
- QueryMapping
- SchemaMapping
- BatchMapping
- Batch Loader and Data loader
- Asynchronous Batch Loading
- MutationMapping

### Goal of Demo
The goal of this demo is to showcase how to use GraphQL with Spring Boot to create a simple API that allows users to create and fetch stories and comments.
```json
{
  "stories": [
    {
      "id": 1,
      "title": "Learning GraphQL",
      "description": "Demo using Spring Boot",
      "comments": [
        {
          "id": 1,
          "content": "This is a great demo!"
        },
        {
          "id": 2,
          "content": "I love GraphQL!"
        }
      ]
    }
  ]
}
```

### Database Schema
- Create `schema.sql` file under `src/main/resources`
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

### Sample Data
- Create `data.sql` file under `src/main/resources`
```sql
INSERT INTO STORIES (title, description) VALUES
    ('User can login', 'Users can login with Google Account'),
    ('User can export entire project', '');

INSERT INTO COMMENTS (story_id, content) VALUES
    (1, 'IPM notes'),
    (1, 'Story accepted'),
    (2, 'What about story history?'),
    (2, 'What about the CSV file content?');
```

### GraphQL schema
- Create a `schema.graphqls` file under `src/main/resources/graphql`
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