# MongoDb
MongoDB is a NoSQL database that stores data in flexible, JSON-like documents. It is a distributed database at its core, 
so high availability, horizontal scaling, and geographic distribution are built in and easy to use.

[Introduction](https://www.mongodb.com/docs/manual/introduction/)

Every object in MongoDB has a unique identifier `_id`. If you don't provide an `_id` field, MongoDB will create one for you.
Every object is a document, and documents are stored in collections. Collections are stored in databases.

We have 3 options for interact with MongoDB:
- `Mongo Shell` (mongosh) is a command-line interface for MongoDB.
- `MongoDB Compass` or `Atlas` is a GUI for MongoDB.
- `MongoDB Drivers` are libraries that allow you to interact with MongoDB from your application.

> I will continue with mongosh and spring data mongodb.

Mapping Charts SQL to MongoDB:
- https://www.mongodb.com/docs/manual/reference/sql-comparison/
- https://www.mongodb.com/docs/manual/reference/sql-aggregation-comparison/

How it works;
- Server: MongoDb has a server that stores data like other databases. (like a server in SQL)
- Database: MongoDb has a database that stores collections. (like a schema in SQL)
- Collection: MongoDb has a collection that stores documents. (like a table in SQL)
- Document: MongoDb has a document that stores fields. (like a row in SQL)
- Field: MongoDb has a field that stores values. (like a column in SQL)

MongoEcosystem:
- Self-Managed: MongoDB Community Server, MongoDB Enterprise Server
  - Community Server: MongoDB Community Server is the free-to-use version of MongoDB.
  - Enterprise Server: MongoDB Enterprise Server is the commercial edition of MongoDB.
- Cloud-Managed: MongoDB Atlas, MongoDB Cloud Manager, MongoDB Ops Manager
  - Atlas: MongoDB Atlas is a fully managed cloud database service that deploys, operates, and scales MongoDB clusters.
  - Cloud Manager: MongoDB Cloud Manager is a cloud-based platform for managing, monitoring, and backing up MongoDB deployments.
  - Ops Manager: MongoDB Ops Manager is a package of management tools for MongoDB Enterprise.
- Mobile: MongoDB Realm, MongoDB Stitch
  - Realm Sync: Sync data between devices and MongoDB Atlas.
  - Realm Database: Local database for mobile devices.

> Also, Mongo has visualization tools like MongoDB Charts, MongoDB Compass, MongoDB BI Connector.


## Downloaded File Structure 
- `bin`: Contains the MongoDB binaries.
  - `mongod`: The MongoDB daemon. It is the primary daemon process for the MongoDB system. It will start the MongoDB server.
  - `mongos`: The MongoDB shard daemon. It is the MongoDB shard process.
  - `mongo`: The MongoDB shell. It is an interactive JavaScript interface to MongoDB. It is used to perform administrative tasks and queries.

This files will be in `/usr/local/bin` directory. When we call them from the terminal, we will use `mongod`, `mongos`, `mongo` commands.

For create a database, we need to start the `mongod` process. After that, we can connect to the database with the `mongo` shell.
Before that, we will create a directory for the database files.

```bash
# Create mongo data and log directories
cd /usr/local
sudo mkdir -p mongodb/data/db mongodb/data/log

# Change the owner of the directories
sudo chown -R $USER mongodb

# Start the mongod process
# We can start multiple mongodb instances with different ports.
mongod --dbpath /usr/local/mongodb/data/db --logpath /usr/local/mongodb/data/log/mongod.log --fork --port 27017

# Connect to the server
mongo --port 27017

# Stop the mongod process
mongod --shutdown
```

---

I will use mongodb with docker. Also, we will use `mongosh` for the shell.

```bash
docker run -d -p 27017:27017 --name mongodb mongo

# Connect to the server
docker exec -it mongodb bash

# Connect to the shell
mongosh
```

## Start With Command Line Simple Commands
- `show dbs`: Show all databases.
- `use <db>`: Switch to a database.
- `db`: Show the current database.
  - We will move and operate in the current database.
- `show collections`: Show all collections in the current database.

```bash
db.products.insertOne({name: "A Book", price: 10.99})
db.products.find()
db.products.find().pretty()

db.products.insertOne({name: "Other Stuff", price: 29.99, details: {description: "Good Stuff"}})
db.products.find().pretty()
```

## Big Picture
- Application: Spring Boot Application
  - Frontend
  - Backend
    - Backend will use `Driver` for interact with MongoDB. (Spring Data MongoDB)
- Database: MongoDB
  - `MongoDB server` will get the requests from the application.
  - Server will use the `Storage Engine` for store the data. (WiredTiger is the default storage engine)
  - `Storage Engine` will store or any other operations on the data in the `Database`.