Implementing asynchronous queries with the Java driver using ScalaPlay framework, bound results in immutable Scala collections and pass to Play controller in reactive model.
Datastax(Asynchronous queries with the Java driver)
===================================================================================================================
* Building Reactive Play application with reactive cassandra driver

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
	* CREATE TABLE tweets (key text PRIMARY KEY, user_user varchar, text text, createdat timestamp) WITH comment='Books' AND COMPACT STORAGE AND read_repair_chance = 1.0;
* In case of different port or cluster configuration, you need to change the following configuration in application.conf
	akka-cassandra {
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
	}
* Install Typesafe Activator if you do not have it already. You can get it from here: http://www.playframework.com/download
* Execute `activator clean compile` to build the application
* Execute `activator run` to execute the application
* Now application should be accessible at localhost:9000

-----------------------------------------------------------------------
###References :-
-----------------------------------------------------------------------
* Play Framework 2.3.0 :- http://www.playframework.com/documentation/2.3.0/ScalaAnorm
* Bootstrap 3.1.1 :- http://getbootstrap.com/css/
* Bootswatch :- http://bootswatch.com/yeti/
* WebJars :- http://www.webjars.org/
