package controllers

import play.api.mvc._
import play.api.libs._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import data.Todo
import repositories.TodoRepository

object TodoController extends Controller {
  private val todoRepository = new TodoRepository

  case class TodoModel(title: String,
                       description: String)

  implicit val todoReads: Reads[TodoModel] = (
    (JsPath \ "title"      ).read[String] and
    (JsPath \ "description").read[String]
  )(TodoModel.apply _)

  implicit val todoWrites = new Writes[Todo] {
    override def writes(todo: Todo): JsValue = {
      Json.obj(
        "id"          -> todo.id,
        "title"       -> todo.title,
        "description" -> todo.description
      )
    }
  }

  def getTodo(id: Long) = Action {
    val todo = todoRepository.tryGetTodoById(id)
    if(todo.isDefined) Ok(Json.toJson(todo))
    else               NotFound
  }

  def createTodo = Action(parse.json) { implicit request =>
    withBodyValidation { model =>
      todoRepository.create(Todo(Option.empty, model.title, model.description))
      Created
    }
  }

  def editTodo(id: Long) = Action(parse.json) { implicit request =>
    withBodyValidation { model =>
      todoRepository.update(Todo(Option(id), model.title, model.description))
      NoContent
    }
  }

  def deleteTodo(id: Long) = Action {
    todoRepository.delete(id)
    NoContent
  }

  private def withBodyValidation(block: TodoModel => Result)
                                (implicit request: Request[JsValue]): Result = {
    request.body.validate[TodoModel].fold(
      valid = block,
      invalid = ({ error =>
        BadRequest(error.mkString)
      })
    )
  }
}
