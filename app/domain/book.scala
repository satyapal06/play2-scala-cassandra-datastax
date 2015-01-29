package domain

import java.util.Date

case class User(user: String) extends AnyVal
object User {
  implicit def toUser(user: String): User = User(user)
}

case class Description(description: String) extends AnyVal
object Description {
  implicit def toDescription(description: String): Description = Description(description)
}

case class BookId(id: String) extends AnyVal
object BookId {
  implicit def toBookId(id: String): BookId = BookId(id)
}

case class Book(id: BookId, user: User, description: Description, createdAt: Date)