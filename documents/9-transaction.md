# Transaction In MongoDB
Transactions works in sessions. Session is created by client and it is used to group multiple operations into a single transaction. Session is connection to mongosh.

A transaction is a set of operations that are executed as a single unit of work. If any operation in the transaction fails, the entire transaction is rolled back. Transactions are used to ensure data integrity and consistency in a database.

```js
const session = db.getMongo().startSession();

session.startTransaction();
const usersCollection = session.getDatabase("mydb").users;

usersCollection.insertOne({ name: "John" }, { session });
usersCollection.insertOne({ name: "Doe" }, { session });

// We can't see any changes until we commit the transaction. Because we don't look at the changes in the same transaction.
db.users.find({ name: "John" }).pretty();

session.commitTransaction();
session.endSession();
```