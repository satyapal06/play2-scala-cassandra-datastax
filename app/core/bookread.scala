package core

import akka.actor.Actor
import com.datastax.driver.core.{ BoundStatement, Cluster, Row }
import domain.Book
import core.BookReaderActor.{ CountAll, FindAll, FindById }
import com.datastax.driver.core.querybuilder.QueryBuilder

object BookReaderActor {
  case class FindAll(maximum: Int = 100)
  case class FindById(id: String)
  case object CountAll
}

class BookReaderActor(cluster: Cluster) extends Actor {
  val session = cluster.connect(Keyspaces.akkaCassandra)
  val countAll = new BoundStatement(session.prepare("select count(*) from books;"))
  val findById = session.prepare("select * from books where key = ?;")

  import scala.collection.JavaConversions._
  import cassandra.resultset._
  import context.dispatcher
  import akka.pattern.pipe

  def buildBook(r: Row): Book = {
    val id = r.getString("key")
    val user = r.getString("user_user")
    val text = r.getString("description")
    val createdAt = r.getDate("createdat")
    Book(id, user, text, createdAt)
  }

  def receive: Receive = {
    case FindAll(maximum) =>
      val query = QueryBuilder.select().all().from(Keyspaces.akkaCassandra, "books").limit(maximum)
      session.executeAsync(query) map (_.all().map(buildBook).toVector) pipeTo sender
    case FindById(id) =>
      val query = findById.bind(id)
      session.executeAsync(query) map (_.map(buildBook).toVector) pipeTo sender
    case CountAll =>
      session.executeAsync(countAll) map (_.one.getLong(0)) pipeTo sender
  }
}