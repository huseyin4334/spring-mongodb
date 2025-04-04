# MongoDb CRUD Operations
## Introduction
MongoDb if we won't specify a `_id` field, MongoDB will automatically generate an `ObjectId` for the `_id` field.
Also, if collection doesn't exist, MongoDB will create it automatically.

- `C`reate
  - `insertOne`
  - `insertMany`
- `R`ead
  - `find` with `filter`
- `U`pdate
  - `updateOne` with `filter` and `data`
    - it will find the first document that matches the filter and update it
  - `updateMany` with `filter` and `data`
    - it will find all documents that match the filter and update them
  - `replaceOne` with `filter` and `data`
    - it will find the first document that matches the filter and replace it
    - The difference between `updateOne` and `replaceOne` is that `replaceOne` will replace the whole document
    - `updateOne` will update only the fields that are specified in the `data`
- `D`elete
  - `deleteOne` with `filter`
    - it will find the first document that matches the filter and delete it
  - `deleteMany` with `filter`
    - it will find all documents that match the filter and delete them

# Some Special Operators
- `$set`
  - it will add a field to the document
- `$unset`
  - it will remove a field from the document
- `$gt`
  - it will find the documents that have a field greater than the specified value

# Let's Code

```bash
db.flightData.insertOne({
    "departureAirport": "MUC",
    "arrivalAirport": "SFO",
    "aircraft": "Airbus A380",
    "distance": 12000,
    "intercontinental": true
})

db.flightData.insertMany([
    {
        "departureAirport": "MUC",
        "arrivalAirport": "SFO",
        "aircraft": "Airbus A380",
        "distance": 12000,
        "intercontinental": true
    },
    {
        "departureAirport": "LHR",
        "arrivalAirport": "TXL",
        "aircraft": "Airbus A320",
        "distance": 950,
        "intercontinental": false
    }
])

db.flightData.insertOne({departureAirport: "Test", _id: "my-own-id"})

# If we try to insert a document with the same `_id`, we will get an error
db.flightData.insertOne({departureAirport: "Test-2", _id: "my-own-id"})

# it will get an error because marker field is not defined in the document
db.flightData.updateOne({distance: 12000}, {marker: 1})

# it will add marker field to the document
db.flightData.updateOne({distance: 12000}, {$set: {marker: 1}})

db.flightData.find({distance: {$gt: 1000}})

db.flightData.findOne({distance: {$gt: 1000}})

```

# Cursor
- `find` method returns a cursor
  - This method not return a list of documents
  - It returns a cursor that we can iterate over with `it` keyword
  - Shell is javascript based, so we can use `forEach` method to iterate over the cursor
- `findOne` method returns a document

```bash
db.users.insertMany([
    {name: "Max", age: 29},
    {name: "Manu", age: 30},
    ... (1000 more)
])

# the default limit is 20
db.users.find() # it will return a cursor
db.users.find().toArray() # it will return a list of documents

db.users.find().forEach(function(doc, index) {
    printjson(doc)
});
```

# Projection
- `projection` is used to specify which fields we want to get back

```bash
# When we set 1 to a field, it means we want to get that field
# When we set 0 to a field, it means we don't want to get that field
# If we don't set _id field to 0, it will return _id field by default. The other fields are not returned by default
db.flightData.find({distance: {$gt: 1000}}, {departureAirport: 1, arrivalAirport: 1, _id: 0})
```

# Embedded Documents
Embedded documents are documents inside another document. (These documents can be only 1 or a list of documents)
Mongo allows us to store documents inside another document that up to 100 levels deep.
Max document size is 16MB.

```bash
db.flightData.insertOne({
    "departureAirport": "MUC",
    "arrivalAirport": "SFO",
    "aircraft": {
        "model": "Airbus A380",
        "maxPassengers": 853
    },
    "distance": 12000,
    "intercontinental": true
})

db.flightData.find({aircraft.model: "Airbus A380"})

db.flightData.updateOne({}, {$set: {aircraft: {company: {name: "Airbus"}}}})

db.flightData.updateOne({$set: {aircraft: {company: {airPlains: ["A380", "A320"]}}}})

# find the documents that have A380 in the airPlains field
db.flightData.find({"aircraft.company.airPlains": "A380"})
```

# Clear Database and Collection

```bash
db.dropDatabase()

db.flightData.drop()
```