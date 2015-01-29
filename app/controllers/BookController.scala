package controllers

import core.BookReaderActor
import core.BookWriterActor
import core.ConfigCassandraCluster
import domain.Book
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.Duration
import akka.actor.ActorSystem
import java.util.concurrent.TimeoutException
import scala.concurrent.duration._
import akka.actor.ActorSystem
import core.ConfigCassandraCluster
import domain._
import play.api._
import play.api.mvc._
import akka.actor.Props
import core.BookReaderActor.{ CountAll, FindAll, FindById }
import core.BookWriterActor.{ mkBook }
import scala.util.{ Success, Failure }
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc.Flash

class BookController extends Controller with ConfigCassandraCluster {
  val logger: Logger = Logger(this.getClass())

  implicit lazy val system = ActorSystem()

  private val bookForm: Form[Book] = Form(
    mapping(
      "id" -> mapping(
        "id" -> nonEmptyText)(BookId.apply)(BookId.unapply),
      "user" -> mapping(
        "user" -> nonEmptyText)(User.apply)(User.unapply),
      "description" -> mapping(
        "description" -> nonEmptyText)(Description.apply)(Description.unapply),
      "createdAt" -> date("mm/dd/yyyy"))(Book.apply)(Book.unapply))

  def save = Action.async { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.book.create(formWithErrors))),
      book => {
        val write = system.actorOf(Props(new BookWriterActor(cluster)))
        implicit val timeout = Timeout(10 seconds)
        val futureResults = (write ? book)
        futureResults.map {
          result =>
            Redirect(routes.BookController.list())
        }.recover {
          case e: TimeoutException => InternalServerError(e.getMessage)
          case e: Exception => InternalServerError(e.getMessage)
        }
      })
  }

  def update = Action.async { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.book.create(formWithErrors))),
      book => {
        val write = system.actorOf(Props(new BookWriterActor(cluster)))
        implicit val timeout = Timeout(10 seconds)
        val futureResults = (write ? book)
        futureResults.map {
          result =>
            Redirect(routes.BookController.list())
        }.recover {
          case e: TimeoutException => InternalServerError(e.getMessage)
          case e: Exception => InternalServerError(e.getMessage)
        }
      })
  }

  def createBook = Action {
    Ok(views.html.book.create(bookForm))
  }

  def list(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    val read = system.actorOf(Props(new BookReaderActor(cluster)))
    implicit val timeout = Timeout(10 seconds)
    val futureResults: Future[Any] = (read ? FindAll())
    futureResults.map {
      result => Ok(views.html.book.list(result.asInstanceOf[Vector[Book]]: Vector[Book]))
    }.recover {
      case e: TimeoutException => InternalServerError(e.getMessage)
      case e: Exception => InternalServerError(e.getMessage)
    }
  }

  def edit(id: String) = Action.async { implicit request =>
    val read = system.actorOf(Props(new BookReaderActor(cluster)))
    implicit val timeout = Timeout(10 seconds)
    val futureResults: Future[Any] = (read ? FindById(id))
    futureResults.map {
      book => Ok(views.html.book.edit(id, bookForm.fill((book.asInstanceOf[Vector[Book]]).apply(0))))
    }.recover {
      case e: TimeoutException => InternalServerError(e.getMessage)
      case e: Exception => InternalServerError(e.getMessage)
    }
  }

  def details(id: String) = Action.async { implicit request =>
    val read = system.actorOf(Props(new BookReaderActor(cluster)))
    implicit val timeout = Timeout(10 seconds)
    val futureResults: Future[Any] = (read ? FindById(id))
    futureResults.map {
      book => Ok(views.html.book.details((book.asInstanceOf[Vector[Book]]).apply(0)))
    }.recover {
      case e: TimeoutException => InternalServerError(e.getMessage)
      case e: Exception => InternalServerError(e.getMessage)
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    val write = system.actorOf(Props(new BookWriterActor(cluster)))
    implicit val timeout = Timeout(10 seconds)
    val futureResults: Future[Any] = (write ? id)
    futureResults.map {
      book =>
        println(book)
        Redirect(routes.BookController.list())
    }.recover {
      case e: TimeoutException => InternalServerError(e.getMessage)
      case e: Exception => InternalServerError(e.getMessage)
    }
  }
}

object BookController extends BookController