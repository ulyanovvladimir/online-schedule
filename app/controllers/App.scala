package controllers

import models.Lesson
import play.api.mvc.{Action, Controller}

/**
  * @author Vladimir Ulyanov
  */
object App extends Controller{
  def index = Action {
    //todo filter
    val lessonList = Lesson.all()
    Ok(views.html.index.render(lessonList))
  }
}
