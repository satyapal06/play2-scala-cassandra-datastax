package core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

import akka.actor.Actor
import com.datastax.driver.core.Cluster

import domain.Book
import domain.BookId
import domain.Description
import domain.User

object BookWriterActor {
  def mkBook(USER: String, DESCRIPTION: String): Book = {
    val dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")
    val cal = Calendar.getInstance();
    val id = BookId.toBookId(UUID.randomUUID.toString)
    val user = User.toUser(USER)
    val text = Description.toDescription(DESCRIPTION)
    val createdAt = dateFormat.parse(dateFormat.format(cal.getTime))
    Book(id, user, text, createdAt)
  }
}

class BookWriterActor(cluster: Cluster) extends Actor {
  val session = cluster.connect(Keyspaces.akkaCassandra)
  val preparedStatement = session.prepare("INSERT INTO books(key, user_user, description, createdat) VALUES (?, ?, ?, ?);")
  val deletePreparedStatement = session.prepare("delete from books where key = ?;")

  def saveBook(book: Book): Unit =
    session.executeAsync(preparedStatement.bind(book.id.id, book.user.user, book.description.description, book.createdAt))

  def deleteBook(id: String): Unit =
    session.executeAsync(deletePreparedStatement.bind(id));

  def receive: Receive = {
    case books: Vector[Book] =>
      books.foreach(saveBook)
      sender ! "$books.size records executed successfully!"
    case book: Book =>
      saveBook(book)
      sender ! "Given record executed successfully!"
    case id: String =>
      deleteBook(id)
      sender ! "Given record has been deleted successfully!"
  }
}