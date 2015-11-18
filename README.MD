# Working with the Spring transactions #

For now code is written only for READ_COMMITED isolation level.
PostgreSQL is used as RDBMS.

## Case ##
There are two transactions, first and second, and second starts definitely after the first one.

In first transaction we select some data, make an update and sleep.
Time for sleeping is enough to be sure, that second transaction is already done.
Meanwhile, 2nd transaction sleeps a little and then select the same data, updates it and trying to commit the changes.

## Issue ##
In normal situation
(using JDBC, look at [jdbc-transaction-isolation](https://github.com/alex-on-java/jdbc-transaction-isolation) project)
second transactions should wait because of lock, and commits just after the first one is committed.
But here second commits easily, and the final data would be from first transaction, not the second.

I'm not sure, why this occures, but I will find out.

#### Run

To run tests, use `mvn test` from the project folder



