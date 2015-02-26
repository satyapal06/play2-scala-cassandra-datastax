Implementing asynchronous queries with the Java driver using ScalaPlay framework, bound results in immutable Scala collections and pass to Play controller in reactive model.
[Datastax(Asynchronous queries with the Java driver)](http://www.datastax.com/dev/blog/java-driver-async-queries)
===================================================================================================================
Building Reactive Play application with reactive cassandra driver
-----------------------------------------------------------------------
###Get this application on your computer :-
-----------------------------------------------------------------------
* Download zip file or Clone the project on your computer
* Extract the downloaded zip/cloned file to your computer
* To run the Play framework 2.3.0, you need JDK 6 or later
* Install cassandra on your computer. You can refer instruction for [linux](https://www.digitalocean.com/community/tutorials/how-to-install-cassandra-and-run-a-single-node-cluster-on-a-ubuntu-vps), [windows](http://support.qualityunit.com/249500-Cassandra-installation-on-Windows-7) and [Node and Cluster Configuration (cassandra.yaml)](http://www.datastax.com/docs/1.0/configuration/node_configuration)
* Run fallowing cqls.
	* CREATE KEYSPACE akkacassandra WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 3};
	* DROP TABLE books;
	* CREATE TABLE books (key text PRIMARY KEY, user_user varchar, text text, createdat timestamp) WITH comment='Books' AND COMPACT STORAGE AND read_repair_chance = 1.0;
* In case of different port or cluster configuration, you need to change the following configuration in application.conf
	<pre>akka-cassandra {
	  main {
	    db {
	      cassandra {
	        port: 9042
	        hosts: [
	          "127.0.0.1"
	        ]
	      }
	    }
	  }
	}</pre>
* Install Typesafe Activator if you do not have it already. You can get it from here: http://www.playframework.com/download
* Execute `activator clean compile` to build the application
* Execute `activator run` to execute the application
* Now application should be accessible at localhost:9000

Overview
----------------

* Requesting for list of book, application route the request to BookController list method. List method is handling asynchronous request coming from client using Action.asynch which gives us ability for calling an external web service using the play.api.libs.WS API, or using Akka to schedule asynchronous tasks or to communicate with actors using play.api.libs.Akka. 
 
* For this tutorial we are using Akka system, in the list method we are scheduling asynchronous task by using Future witch is communicating with BookReaderActor. In BookReaderActor companion object, I have defined the FindAll, FindById and CountAll messages that our actor will react. Next up, we are going to pass FindAll message to BookReaderActor. BookReaderActor receive the message, now the actor execute the corresponding block in which I have prepared a Cassandra Select Query, execute (asynchronously) by taking advantage of the asynchronous nature of the Cassandra driver, map the rows returned from that query execution to turn them into the books; and then pipe the result to the sender.

* In case of play.api.libs.WS we need to integrate Spray API for client.
 
Futures
----------
 
A Future is an object holding a value which may become available at some point. This value is usually the result of some other computation:
If the computation has not yet completed, we say that the Future is not completed.
If the computation has completed with a value or with an exception, we say that the Future is completed.
Completion can take one of two forms:
When a Future is completed with a value, we say that the future was successfully completed with that value.
When a Future is completed with an exception thrown by the computation, we say that the Future was failed with that exception.

Asynchronous queries with the Java driver
--------------------------------------------

The DataStax Java driver for Cassandra uses an asynchronous architecture. This allows client code to get query results in a non-blocking way, via Future instances. In this post, we take a closer look at this concept, and use it to implement a client-side equivalent to the SELECT...IN query.

Asynchronous query result: ResultSetFuture
---------------------------------------------

Apart from sending the query to Cassandra, the driver registers an internal ResponseHandler, which will process the response when it is available. It then gives control back to the caller, returning a ResultSetFuture which represents the future completion of the query. This object implements Java’s Future; at this point, its isDone method returns false.
When Cassandra returns the response, the driver notifies the ResponseHandler (many handlers can be registered for different queries, so the match is made with the stream id, a unique identifier that was initially sent with the request). The handler will in turn complete the future. This is all executed in an I/O thread managed by Netty, the underlying networking framework.
At some point, the client code will invoke the future’s get method to obtain the result. This will block if the future has not yet completed.

-----------------------------------------------------------------------
###References :-
-----------------------------------------------------------------------
* [Asynchronous queries with the Java driver](http://www.datastax.com/dev/blog/java-driver-async-queries)
* [Play Framework 2.3.0](http://www.playframework.com/documentation/2.3.0)
* [Bootstrap 3.1.1](http://getbootstrap.com/css/)
* [Bootswatch](http://bootswatch.com/yeti/)
* [WebJars](http://www.webjars.org/)
