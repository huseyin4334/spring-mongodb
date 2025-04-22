# Authantication And Authorization
Roles can be;
- Database Users
  - `readWrite`
  - `read`
- Database Admins
  - `dbAdmin`
  - `userAdmin`
  - `dbOwner`
- All Database Roles
  - `readAnyDatabase`
  - `readWriteAnyDatabase`
  - `dbAdminAnyDatabase`
  - `userAdminAnyDatabase`
- Cluster Admins
  - `clusterAdmin`
  - `clusterMonitor`
  - `clusterManager`
- Super User
  - `dbOwner` (admin)
  - `userAdmin` (admin)
  - `root`
  - `userAdminAnyDatabase`

Create User
- `db.createUser({user: "username", pwd: "password", roles: [{role: "readWrite", db: "database"}]})`
- `db.createUser({user: "username", pwd: "password", roles: ["readWrite"]})`

Update User
- `db.updateUser("username", roles: [{role: "readWrite", db: "database"}])`

Authentication
- `db.auth("username", "password")`
- `mongo --username username --password password`
- `mongo --authenticationDatabase database`

# Capped Collections
Capped collections are fixed-size collections that automatically overwrite the oldest documents when they reach their maximum size. They are useful for scenarios where you want to maintain a rolling log of data, such as in logging or monitoring applications.

- `db.createCollection("cappedCollection", { capped: true, size: 1000000 })`
- `db.createCollection("cappedCollection", { capped: true, maxDocuments: 1000 })`

# Replicatset And Sharding
Replication is the process of synchronizing data across multiple MongoDB instances to ensure high availability and data redundancy. A replica set is a group of MongoDB servers that maintain the same data set, providing fault tolerance and automatic failover.

Sharding is the process of distributing data across multiple servers to ensure horizontal scalability. It allows MongoDB to handle large amounts of data and high throughput by partitioning data into smaller, more manageable chunks. It uses `shard key` to determine how data is distributed across shards. The shard key is a field or fields in the document that MongoDB uses to partition the data. It is important to choose a shard key that provides even distribution of data and allows for efficient queries.

[MongoDB Sharding](https://www.mongodb.com/docs/manual/core/sharding-shard-a-collection/)