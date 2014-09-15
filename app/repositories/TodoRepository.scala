package repositories

import java.util.concurrent.ConcurrentHashMap

import infrastructure.IdGenerator
import data.Todo

class TodoRepository {
  private val todos = new ConcurrentHashMap[Long, Todo]
  private val idGenerator = new IdGenerator(0)

  def tryGetTodoById(id: Long) =
    Option(todos.get(id))

  def create(todo: Todo) = {
    val newId = idGenerator.next()
    val newTodo = todo.copy(id = Option(newId))
    todos.put(newId, newTodo)
  }

  def update(todo: Todo) {
    todos.put(todo.id.get, todo)
  }

  def delete(id: Long) {
    todos.remove(id)
  }
}
