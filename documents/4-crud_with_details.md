# CREATE
- insertOne
- insertMany
- insert

When we insert with insertOne or insertMany, we will get an ObjectId for inserted documents. 
If we want to insert a document with insert method, it won't return an ObjectId.
It's only gave us a success count. When we insert many with insert it will get more detail if there is an error.

## Ordered Insert
When we insertMany and if the command get an error on 3rd document, it will be inserted 1st and 2nd documents.
If we want to insert documents individually, we can use `ordered` option with `false`.
If we set false, it will insert 1st and 2nd documents, after that it will get an error for 3rd document, 
and it will insert 4th document.
Because the default value of the `ordered` option is `true`.

```bash
db.flightData.insertMany([
    {departureAirport: "Test", arrivalAirport: "Test", airline: "Test", stops: 0},
    {departureAirport: "Test-2", arrivalAirport: "Test-2", airline: "Test-2", stops: 1},
    {departureAirport: "Test-3", arrivalAirport: "Test-3", airline: "Test-3", stops: 2},
    {departureAirport: "Test-4", arrivalAirport: "Test-4", airline: "Test-4", stops: 3}
], {ordered: false})
```

## Understanding Write Concern
- Client
- Server
- Storage Engine
  - Memory
  - Journal
  - Disk

---

I will explain the default behavior.
When we execute a request to the server, it will send this command to the storage engine.
Storage engine will write the data to the memory.
After that, it will write the data to the journal. Journal is like a todo list for the disk.
When we inserted a data it will write to the journal quickly after controls. 
But you know if we write disk directly, it will write on disk, update the index, and it will be slow with other processes too.

---

Write concern is a way to control the behavior of the write operation.

When we execute a request, it will be sent to the server with a write concern. (`{w: 1, j: true, wtimeout: 200}`)
- `w` is the number of nodes that the data will be written to.
- `j` is the journaling option. If we set `true`, it will write to the journal, and it will accept the write operation.
- `wtimeout` is the time that the server will wait for the write operation.

```bash
db.names.insertOne({name: "Huseyin"}, {writeConcern: {w: 0}})
# {acknowledged: false}

db.names.insertOne({name: "Ahmet"}, {writeConcern: {w: 1, j: false}})
# {acknowledged: true, insertedId: ObjectId("5f3f4")}
```

> Atomicity is document level in MongoDB. If we insert a document, it will be inserted or not.


## Import Data
- `mongoimport` is a command-line tool that imports content from an Extended JSON, CSV, or TSV export created by `mongoexport`, or potentially, another third-party export tool.
- `-d` is the database name
- `-c` is the collection name
- `--jsonArray` is used to import a JSON array ([{}, {}, {}])
- `--drop` is used to drop the collection before importing the data (if the collection exists)

```bash
mongoimport --db=flightData --collection=flights --file=flights.json

mongoimport flights.json -d flightData -c flights --jsonArray --drop
```


# READ
We will use `find` method to read data from the database.

- Normal equality query
  - db.collection.find({field: value}, projection)
  - db.collection.find({age: 32})
- Comparison query
  - db.collection.find({field: {operator: value}}, projection)
  - db.collection.find({field: {$gt: value}}, projection)

## Operators
Operators are used to some different cases.

- Read
  - Query Selectors
  - Projection Operators
    - Projection Operators are used to select the fields that we want to return.
- Update
  - Fields Operators
    - It's used to update the fields in the document.
  - Arrays
    - Array Operators are used to update the arrays in the document.
- Aggregation
  - Pipeline Stages
  - Pipeline Operators

> [Operators](https://www.mongodb.com/docs/manual/reference/operator/)


## Query Selectors

```json

{
    "field": "value",
    "field2": ["value2", "value3"],
    "field3": {
        "field4": "value4",
        "field5": {
            "inner": 10
        }
    },
    "field6": 7,
    "createdAt": "2023-10-01T00:00:00Z",
    "updatedAt": "2023-10-01T00:00:00Z",
}
```

```bash
db.collection.find({field: value})
db.collection.find({field: {$gt: value}})
db.collection.find({field: {$lt: value}})

# Get all documents that field3.field4 is equal to value or field3.field5 is greater than 8
db.collection.find({$or: [{"field3.field4": "value"}, {"field3.field5": {$gt: 8}}]})
# This will work with or operator too. Because we can't use same field with 1 query. If we use mongo get it with or operator.
db.collection.find({"field2": "value3"}, {"field2": "value2"})

# Get all documents that field3.field4 is not equal to value and field3.field5 is less than 8
db.collection.find({$nor: [{"field3.field4": "value"}, {"field3.field5": {$gt: 8}}]})


# These queries are equivalent
db.collection.find({$and: [{"field3.field4": "value"}, {"field3.field5": {$gt: 8}}]})
db.collection.find({"field3.field4": "value", "field3.field5": {$gt: 8}})

db.collection.find({"field6": {$not: { $eq: 60}}})
db.collection.find({"field6": {$ne: 60}})
```

```bash
# $exists operator
# true means that the field is exist in the document.
# false means that the field is not exist in the document.
db.collection.find({field: {$exists: true}})
db.collection.find({field: {$exists: false, $ne: null}})

# $type operator
# type is a number that represents the type of the field.
# we can see the types in the [MongoDB documentation](https://www.mongodb.com/docs/manual/reference/operator/query/type/)
db.collection.find({field: {$type: 1}}) # 1 is for double
db.collection.find({field: {$type: "double"}}) # these are equivalent

db.collection.find({field: {$type: ["double", "string"]}}) # this will return double and string types


# $regex operator
db.collection.find({field: {$regex: /value/}}) # this will return all documents that field contains value
db.collection.find({field: {$regex: /value/i}}) # this will return all documents that field contains value (case insensitive)
db.collection.find({field: {$regex: /^value/}}) # this will return all documents that field starts with value
db.collection.find({field: {$regex: /value$/}}) # this will return all documents that field ends with value


# $expr operator
# $expr operator is used to compare the fields in the document.
# this will always get true or false from the condition.
db.collection.find({$expr: {$eq: ["$field1", "$field2"]}}) # this will return all documents that field1 is equal to field2

# We create a condition with @cond operator. it has 3 parameters. (if, then, else)
# we define if condition with {$gte: ["$field4", 10]} (if field4 is greater than 10)
# then we will subtract 1 from field4. else we will return field4.
# Condition will return a value and we will execute $expr condition. {$gt: [condition, field5]} (if condition is greater than field5)
# If this condition is true, it will return the document.
db.collection.find({$expr: {$gt: [{$cond: {if: {$gte: ["$field4", 10]}, then: {$subtract: ["$field4", 1]}, else: "$field4"} }, "$field5"] } })


# $size operator
db.collection.find({field2: {$size: 2}}) # this will return all documents that field2 has 2 elements
db.collection.find({field2: {$size: {$gt: 2}}}) # this will return all documents that field2 has more than 2 elements

# $all operator
# this query won't return a match. Because mongo control the order of the array.
# If we don't care about the order of the array, we can use $all operator.
db.collection.find({field2: ["value3", "value2"]})
db.collection.find({field2: {$all: ["value3", "value2"]}}) # this will return all documents that field2 has value3 and value2

# $elemMatch operator
db.collection.find({$and: [{"fieldx.title": "Sports"}, "fieldx.score": {$gt: 10}}]}) 
# this will return all documents that fieldx has title Sports and score greater than 10

db.collection.find({fieldx: {$elemMatch: {title: "Sports", score: {$gt: 10}}}}) 
# this will return all documents that fieldx has title Sports and score greater than 10

db.collection.find({fieldx: {$elemMatch: {score: {$gt: 10, $lt: 20}}}})
# this will return all documents that fieldx has score greater than 10 and less than 20


# Cursor & Sorting & Limiting & Skipping
# Cursor is a pointer to the result set of a query.
# It is used to iterate through the result set and retrieve documents one by one.

# Let's assume that we have a collection with 1000 documents.

const collection = db.collection.find()
collection.hasNext()
# this can be true or false.

collection.next()
# this will return the next document in the cursor.

# this will sort the documents by field in ascending order
# 1 is for ascending order and -1 is for descending order
db.collection.find().sort({"field": 1, "fieldx.rating": -1})

# It will skip the first 10 documents and return the next 10 documents. (The default skip is 10.)
# The cursor will give us documents how many write in limit. (10 in this case) (The page size)
db.collection.find().sort({"field": 1, "fieldx.rating": -1}).skip().limit()

# Projection
db.collection.find({}, {"field": 1, "fieldx.rating": 1, "_id": 0}) # this will return all documents with field and fieldx.rating

# this will return field2 with only 1 element. (the first element)
# It will control the values. After that when it finds the match, it will return the first element.
db.collection.find({"field2": {$all: ["value2", "valueX"]}}, {"field2.$": 1})

# We will see some rows with field2 and without field2.
# Because we got rows matched with field2 condition. After that, we did a projection with $elemMatch.
# It will control the array if the array has a value with valueX, it will send the row with field2.
db.collection.find({"field2": "value2"}}, {"field2": {$elemMatch: {$eq: "valueX"}}})

# $slice operator
# $slice operator is used to limit the number of elements in an array.
# It can be used in projection to limit the number of elements returned in an array field.
# It can also be used in the query to limit the number of elements returned in an array field.

db.collection.find({}, {"field2": {$slice: 2}}) # this will return the first 2 elements in field2 array
db.collection.find({}, {"field2": {$slice: -2}}) # this will return the last 2 elements in field2 array

db.collection.find({}, {"field2": {$slice: [2, 3]}}) # this will return the 3 elements starting from index 2 in field2 array

```

# UPDATE
- `$set` operator
  - `db.collection.updateOne({field: value}, {$set: {field2: value2, field3: value3}})`
  - We can set multiple fields with `$set` operator.
  - If the field is not exist in the document, it will create a new field with the value.
  - But if it's exist, it won't override the field.
- Incrementing and Decrementing
  - `$inc` operator uses to increment or decrement a field by a specified value.
  - `$inc` operator can be used with `$set` operator.
  - `db.users.updateOne({age: 30}, {$inc: {age: 1}})`
    - age will be incremented by 1.
  - `db.users.updateOne({age: 30}, {$inc: {age: -1}})`
    - age will be decremented by 1.
- `$min`, `$max`, `$mul`
  - `$min` operator is used to update a field only if the specified value is less than the current value of the field.
    - `db.users.updateOne({age: 30}, {$min: {age: 25}})`
      - age will be updated to 25 only if the current value of age is greater than 25. 
      - We filtered documents that age is 30. Because of that, age fields will change with 25
    - `db.users.updateOne({age: 20}, {$min: {age: 25}})`
      - age won't change because the current value of age is less than 25.
  - `$max` operator is same with `$min` operator.
  - `$mul` operator is used to multiply a field by a specified value.
    - `db.users.updateOne({age: 30}, {$mul: {age: 2}})`
      - age will be multiplied by 2.
- Getting Rid of Fields (Deleting Fields)
  - `$unset` operator is used to remove a field from a document.
    - `db.users.updateOne({age: 30}, {$unset: {age: ""}})`
      - age field will be removed from the document.
- Renaming Fields
  - `$rename` operator is used to rename a field in a document.
    - `db.users.updateOne({age: 30}, {$rename: {age: "newAge"}})`
      - age field will be renamed to newAge.
- `upsert()` 
  - `upsert` is a combination of update and insert.
  - If the document is not exist, it will create a new document with the specified fields.
  - If the document is exist, it will update the document with the specified fields.
  - `db.users.updateOne({name: "Maria"}, {$set: {age: 35}}, {upsert: true})`
    - If the document is not exist, it will create a new document with age: 35 and name: Maria.
- Update Array Elements
  - Match and update or set array fields
    - `db.users.updateMany({$and: [{"hobbies.title": "Sports"}, {"hobbies.freq": {$gt: 4}}]}, {$set: {"hobbies.$[].highFreq": true}})`
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$set: {"hobbies.$[].highFreq": true}})`
      - These queries are same.
      - This will update the first element in the hobbies array that matches the condition.
      - If we want to update all elements in the array, we can use `$[]` operator.
      - The difference with `$` and `$[]` is that `$` operator will update the first element that matches the condition.
      - When we use `updateOne` we will use `$`. When we use `updateMany` we will use `$[]`.
      - But don't forget we filtered documents. Not array elements. Because of that, when we update or insert something in array, it will applied for all elements.
  - match and update or set specific elements of fields
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$set: {"hobbies.$[el].highFreq": true}}, {arrayFilters: [{"el.freq": {$gt: 4}}]})`
      - We can use `arrayFilters` to filter the elements in the array.
      - In this case, we filtered the elements with `freq` greater than 4.
      - This means, only will be set `highFreq` elements on the array that has `freq` greater than 4.	 Others won't have this variable.
  - add new array elements
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$push: {hobbies: {title: "Hokey", freq: 3}}})`
      - We can use `push` operator to add a new element to the array.
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$push: {hobbies: {$each: [{title: "Hokey", freq: 3}, {title: "Basketball", freq: 5}], $sort: {freq: -1}, $slice: 10}}})`
      - We can use `each` operator to add multiple elements to the array.
      - When we add `$sort`, it will sort the array by the specified field. It will change the order of the array in the document and database.
      - When we add `$slice`, it will limit the number of elements in the array. It will remove other elements from the array in the document and database.
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$addToSet: {hobbies: {$each: [{title: "Hokey", freq: 3}, {title: "Basketball", freq: 5}]}}})`
      - `addtoSet` controls the array that it has the same element or not. If element is exist in the array, it won't add it again.
      - If we use `push`, it will add the element to the array even if it's exist in the array.
  - match and delete array elements
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$pull: {hobbies: {title: "Hokey"}}})`
      - It will remove all elements that match the condition.
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$pull: {hobbies: {title: "Hokey", freq: 3}}})`
      - We can use `pull` operator to remove an element from the array with multiple conditions.
    - `db.users.updateMany({hobbies: {$elemMatch: {"title": "Sports", "freq": {$gt: 4}}}}, {$pop: {hobbies: 1}})`
      - It will delete last element of the array. If we use -1, it will delete first element of the array.