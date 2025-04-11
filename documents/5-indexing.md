# Index
`db.products.find({seller: "John Doe"})`

- No Index
  - COLLSCAN
    - Scan all documents in the collection
- Index
  - IXSCAN
    - Scan the index to find matching documents
    - Every index is sorted in a specific order 


```bash
# Query plan
# explain() method returns the query plan for a given query
# Also we can call explain() with different verbosity levels: "queryPlanner", "executionStats", "allPlansExecution"
# "queryPlanner" is the default verbosity level. (executed query + winning plan)
# "executionStats" returns the execution stats for the query. This returns the number of documents scanned, number of documents returned, and other execution stats. (executed query + winning plan + possibly rejected plans)
# "allPlansExecution" returns the execution stats for all plans considered by the query planner. (We can see the other plans that were considered but not chosen on the query planner with rejectedPlans field) (executed query + winning plan + winning plan decision process)
db.products.explain().find({seller: "John Doe"})

#  "queryPlanner": {
#    ....
#    "winningPlan": {
#      "stage": "COLLSCAN",
#      "filter": {
#        "$eq": {
#          "seller": "John Doe"
#        }
#      },
#      "direction": "forward"
#    }
#  }

# Stages
# - COLLSCAN: Collection scan, scan all documents in the collection
# - IXSCAN: Index scan, scan the index to find matching documents
# - FETCH: Fetch the documents from the collection using the index
# - SORT: Sort the documents in the result set
# - LIMIT: Limit the number of documents in the result set
# - SKIP: Skip the first n documents in the result set
# - UNION: Combine the results of multiple queries
# - MERGE: Merge the results of multiple queries

```

```bash
# Create an index on the seller field
db.products.createIndex({seller: 1}) # 1 for ascending order, -1 for descending order

# Drop the index
db.products.dropIndex({seller: 1}) # 1 for ascending order, -1 for descending order
db.products.dropIndexes() # Drop all indexes on the collection

# List all indexes on the collection
db.products.getIndexes()
db.products.getIndexKeys() # List all index keys on the collection

db.products.getIndexSpecs() # List all index specs on the collection. (Index specs include the index keys, options, and other information about the index)
```

```bash
# Create a compound index on the seller and price fields
# Compound indexes are used to index multiple fields in a single index. 
# The order of the fields in the index matters. The first field in the index is the most important field for the index.
db.products.createIndex({seller: 1, price: -1}) # 1 for ascending order, -1 for descending order

# When we execute {seller: "John Doe", price: 100} query, the index will be used to find the documents that match the documents.
# When we execute {seller: "John Doe"} query, the index will be used to find the documents that match the documents too.
# When we execute {price: 100} query, mongodb will not use the index because the first field in the index is seller.
```

> When we want to sort the documents. We can use the indexes for this purpose too. Because the indexes are sorted in a specific order.

> MongoDB has a default index on the _id field. The _id field is a unique identifier for each document in the collection. 
> The _id field is automatically created when we insert a document into the collection.

```bash
# Configure the indexes

# Unique indexes are used to enforce uniqueness on a field or a combination of fields in the collection.
# But if the documents already have duplicate values, the index creation will fail.
db.products.createIndex({seller: 1}, {unique: true})

# partialFilterExpression: Create an index on a subset of documents in the collection.
# This means we will give a filter condition to the index. After that, if the documents match the filter condition, the index will be created on those documents.
# This is useful when we want to create an index on a field that is not present in all documents in the collection.
db.products.createIndex({seller: 1}, {partialFilterExpression: {price: {$gt: 100}}}) # Create an index on the seller field for documents where the price is greater than 100

db.product.find({seller: "John Doe", price: 100}) # This will use the index because the price is greater than 100
db.product.find({seller: "John Doe", price: 50}) # This will not use the index because the price is not greater than 100
db.product.find({seller: "John Doe"}) # This will not use the index because the condition is not met


# An example
db.users.createIndex({email: 1}, {unique: true}) # Create a unique index on the email field
db.users.insertOne({name: "Anna"}) # This will work
db.users.insertOne({name: "Dave"}) # This will get an error because the email field is not unique

# In mongoDB, null values or missing values are considered as unique values.

db.dropIndex({email: 1}) # Drop the index on the email field

db.users.createIndex({email: 1}, {unique: true, partialFilterExpression: {email: {$exists: true}}}) 
# Create a unique index on the email field for documents where the email field exists. If the email field does not exist, the index will not be created on that document.

db.users.insertOne({name: "Dave"}) # This will work now.
```

```bash
# TTL (Time to Live) indexes are used to automatically delete documents from the collection after a certain period of time.
# This is useful for storing data that is only relevant for a certain period of time, such as logs or session data.
# TTL indexes are created on a date field. The date field must be of type Date or ISODate.

db.sessions.insertOne({userId: 1, createdAt: new Date()}) # Insert a document with the current date
db.sessions.createIndex({createdAt: 1}, {expireAfterSeconds: 3600}) # Create a TTL index on the createdAt field with an expiration time of 1 hour (3600 seconds)

# After 1 hour, the document will be automatically deleted from the collection.
```

```bash
# Covered Query
# Covered queries are queries that can be answered using only the index without having to look at the actual documents in the collection.
# This is useful for improving query performance because it reduces the amount of data that needs to be read from the disk.

db.users.insertMany([{name: "John Doe", age: 30}, {name: "Jane Doe", age: 25}, {name: "Anna Smith", age: 35}])
db.users.createIndex({name: 1})

db.users.explain("executionStats").find({name: "John Doe"})

# nReturned: 1 # Number of documents returned by the query
# totalKeyExamined: 1 # Number of index keys examined by the query
# totalDocsExamined: 1 # Number of documents examined by the query

db.users.explain("executionStats").find({name: 1}, {_id: 0, name: 1})

# nReturned: 1 # Number of documents returned by the query
# totalKeyExamined: 1 # Number of index keys examined by the query
# totalDocsExamined: 0 # Number of documents examined by the query (0 because the query is covered by the index)
```

```bash
# Multi-Key Indexes
# Multi-key indexes are used to index array fields in the collection.
# When we create a multi-key index on an array field, MongoDB creates an index entry for each element in the array.

# Index on subdocuments
# We can create an index on a field that is an array of subdocuments.
# MongoDB will create an index entry for each subdocument in the array.

db.products.insertOne({name: "John Doe", tags: ["electronics", "gadgets"]}) # Insert a document with an array field
db.products.createIndex({tags: 1}) # Create a multi-key index on the tags field

db.products.find({tags: "electronics"}) # This will use the index because the tags field is an array field
db.products.find({tags: {$in: ["electronics", "gadgets"]}}) # This will use the index because the tags field is an array field

db.users.insertMany([
  {name: "John Doe", hobbies: [{name: "reading"}, {name: "sports"}]},
  {name: "Jane Doe", hobbies: [{name: "cooking"}, {name: "sports"}]},
])

db.users.createIndex({"hobbies": 1})

db.users.find({"hobbies.name": "reading"}) # This won't use the index. Because mongo indexed all documents in the array.
db.users.find({hobbies: {$elemMatch: {name: "reading"}}}) # This will use the index.

# Also mongo don't support compound indexes on array fields in the same index.
```


```bash	
# Text Indexes
# Text indexes are used to index string fields for text search.
# Text indexes are used to perform full-text search on string fields in the collection.

db.tech.insertMany([
  {title: "MongoDB Basics", content: "Learn the basics of MongoDB"},
  {title: "MongoDB Advanced", content: "Learn advanced topics in MongoDB"},
  {title: "MongoDB Performance title", content: "Learn how to improve MongoDB performance"},
])

db.tech.createIndex({content: "text"}) # Create a text index on the content field

db.tech.find({$text: {$search: "performance"}}) # This will use the text index to search for documents that contain the word "MongoDB" in the content field

# Sorting on text indexes
# Text indexes have scoring. The score is a number that indicates how relevant the document is to the search term.
# The higher the score, the more relevant the document is to the search term. Mongo sorts the documents by score.
# We can use the $meta operator to sort the documents by score.

db.tech.find({$text: {$search: "MongoDB"}}, {score: {$meta: "textScore"}})
# This will return the documents that contain the word "MongoDB" in the content field and will show the score for each document.

# We can't delete text indexes with definitions. We should use the name of the index to delete it.
db.tech.dropIndex("content_text") # Drop the text index on the content field


db.tech.createIndex({title: "text", content: "text"}) # Create a compound text index on the title and content fields

db.tech.find({$text: {$search: "title"}})
# this will return the documents that contain the word "title" in the title or content field.


# Exclude words from the index (- keyword)
db.tech.find({$text: {$search: "MongoDB -performance"}}) # This will exclude the word "performance" from the search results.


# Change the default language for the text index
# The default language is english. It uses for stemming and stop words.
db.tech.createIndex({content: "text"}, {default_language: "french"}) # Create a text index on the content field with French as the default language

# Weights
# Weights are used to assign different weights to different fields in the text index. Scores are calculated based on the weights of the fields.
db.tech.createIndex({title: "text", content: "text"}, {weights: {title: 10, content: 5}})

# Default Case sensitivity is false 
db.tech.find({$text: {$seach: "mongodb", $language: "german", $caseSensitive: true}})
```

```bash
# Foreground and Background Indexes

# Foreground index that the index is created while the collection is being used. 
# This can cause performance issues because the collection is locked while the index is being created.

# Background index that the index is created in the background.
# This allows the collection to be used while the index is being created.
# This is useful for large collections that are being used by multiple users.

# The default is foreground index.
db.products.createIndex({seller: 1}, {background: true}) # Create a background index on the seller field

```