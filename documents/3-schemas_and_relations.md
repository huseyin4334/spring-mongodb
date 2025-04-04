# Schemas And Relations
MongoDB doesn't have a schema like SQL databases. It is schema-less. 
But we can define a schema in MongoDB with driver or ORM tools.

## Data Types

- Text: 
  - It is a string of characters.
  - "Max"
- Boolean: 
  - It is a true or false value.
  - true
- Integer: 
  - It is a whole number.
    - `NumberInt`
      - int32
      - 55
    - `NumberLong`
      - This is default integer type in shell.
      - int64
      - 1234567890
    - `NumberDecimal`
      - decimal128
      - 12.99
- ObjectId: 
  - It is a 12-byte identifier. It has timestamp, machine id, process id, and a random incrementing value.
  - ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f")
- Date: 
  - It is a 64-bit integer that represents the number of milliseconds since the Unix epoch.
  - ISODate("2020-08-08T00:00:00Z")
    - new Date()
  - Timestamp
    - new Timestamp()
- Array:
  - It is a list of values.
- Embedded Document:
  - It is a document inside another document.

```mongodb-json
{
  "name": "Max",
  "age": NumberInt(30),
  count: NumberLong(1234567890),
  "isStudent": true,
  "hobbies": ["Reading", "Coding"],
  "address": {
    "city": "Istanbul",
    "country": "Turkey"
  },
  "createdAt": ISODate("2020-08-08T00:00:00Z"),
  "secondId": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "salary": NumberDecimal("12.99"),
  "miliseconds": Timestamp(345345467, 1)
}
```

```bash
db.workers.insertOne({
  "name": "Max",
  "age": NumberInt(30),
  "count": NumberLong(1234567890),
  "isStudent": true,
  "hobbies": ["Reading", "Coding"],
  "address": {
    "city": "Istanbul",
    "country": "Turkey"
  },
  "createdAt": new Date("2020-08-08T00:00:00Z"),
  "secondId": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "salary": NumberDecimal("12.99"),
  "miliseconds": new Timestamp()
})
```

---

- Normal integers (int32) can hold a maximum value of +-2,147,483,647
- Long integers (int64) can hold a maximum value of +-9,223,372,036,854,775,807
- Text can be as long as you want - the limit is the 16mb restriction for the overall document


## Relations With References
In MongoDB, we can use references to establish relationships between documents.

```mongodb-json
{
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "name": "Max",
  "age": NumberInt(30),
  "address": {
    "city": "Istanbul",
    "country": "Turkey"
  }
}

{
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f5f"),
  "title": "A Blog Post",
  "author": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f")
}
```

```bash
db.users.insertOne({
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "name": "Max",
  "age": NumberInt(30),
  "address": {
    "city": "Istanbul",
    "country": "Turkey"
  }
})

db.posts.insertOne({
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f5f"),
  "title": "A Blog Post",
  "author": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f")
})
```

## Merge Relations ($lookup)
In MongoDB, we can merge relations with the `$lookup` operator.
This operator gets 4 parameters:
- from: The collection to join.
- localField: The field from the input documents.
- foreignField: The field from the documents of the "from" collection.
- as: The output field.

users collection has a relation with posts collection. I said the aggregation framework to merge these collections with `_id` and `author` fields.
When it finds the matching documents, it adds the posts array to the users collection.

```bash
db.users.aggregate([
  {
    $lookup: {
      from: "posts",
      localField: "_id",
      foreignField: "author",
      as: "posts"
    }
  }
]).pretty()

# response
{
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "name": "Max",
  "age": NumberInt(30),
  "address": {
    "city": "Istanbul",
    "country": "Turkey"
  },
  "posts": [
    {
      "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f5f"),
      "title": "A Blog Post",
      "author": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f")
    }
  ]
}
```

---

## Collection Document Validation
In MongoDB, we can validate documents with JSON Schema.

```mongodb-json
{
  "_id": ObjectId("5f2f4b3f3f4f4f4f4f4f4f4f"),
  "title": "A Blog Post",
  "text": "This is a blog post.",
  "creator": ObjectId("5f2f4b3f3f4f4f4f4f4f4fxx"),
  "comments": [
    {
      "text": "I like this blog post.",
      "author": ObjectId("5f2f4b3f3f4f4f4f4f4f4fyy")
    }
  ]
}
```

---

- `$jsonSchema` is the key to define the schema.
- `bsonType` is the type of the field. (object, string, array, objectId, etc.)
- `required` is the required fields. (title, text, creator) Also we can add `comments` as an optional field. 
- But we can't insert a document with different fields that are not in the schema.

```bash
db.createCollection("posts", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "text", "creator"],
      properties: {
        title: {
          bsonType: "string",
          description: "must be a string and is required"
        },
        text: {
          bsonType: "string",
          description: "must be a string and is required"
        },
        creator: {
          bsonType: "objectId",
          description: "must be an objectId and is required"
        },
        comments: {
          bsonType: "array",
          description: "must be an array and is not required",
          items: {
            bsonType: "object",
            required: ["text", "author"],
            properties: {
              text: {
                bsonType: "string",
                description: "must be a string and is required"
              },
            }
          }
        }
      }
    }
  }
})
```

---

Update the validation rules with the `collMod` command.
`runCommand` is a method to run commands in MongoDB. `collMod` is the key to modify the collection.

```bash
db.runCommand({
  collMod: "posts",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["title", "text", "creator", "createdAt"],
      properties: {
        title: {
          bsonType: "string",
          description: "must be a string and is required"
        },
        text: {
          bsonType: "string",
          description: "must be a string and is required"
        },
        creator: {
          bsonType: "objectId",
          description: "must be an objectId and is required"
        },
        comments: {
          bsonType: "array",
          description: "must be an array and is not required",
          items: {
            bsonType: "object",
            required: ["text", "author"],
            properties: {
              text: {
                bsonType: "string",
                description: "must be a string and is required"
              },
            }
          }
        },
        createdAt: {
          bsonType: "date",
          description: "must be a date and is required"
        }
      }
    }
  },
  validationAction: "error" # default is "warn". It can be "error" or "warn"
})
```