package controllers

import models.Lesson
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConversions._

/**
  * @author Vladimir Ulyanov
  */
object App extends Controller {

  case class FilterData(groupNumber: Option[String], instructor: Option[String])


  val filterForm = Form(
    mapping(
      "groupNumber" -> optional(text),
      "instructor" -> optional(text)
    )(FilterData.apply)(FilterData.unapply)
  )

  def allGroups = Lesson.all().toList.map(lesson => lesson.getGroupNumber).distinct

  def groupSchedule() = Action {
    Ok("todo")
  }

  def index() = Action { implicit request =>

    filterForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        val lessonList = Lesson.all()
        BadRequest(views.html.index(formWithErrors, lessonList, allGroups))
      },
      userData => {
        userData match {
          case FilterData(None, _) =>
            Logger.debug("NO GROUP")
            val lessonList = Lesson.all()
            Ok(views.html.index(filterForm.fill(userData), lessonList))
          case FilterData(Some(group), _) =>
            Logger.debug(group)
            val lessonList = Lesson.find.where().ilike("groupNumber", group).findList()
            Ok(views.html.index(filterForm.fill(userData), lessonList))
          case _ =>
            val lessonList = Lesson.all()
            Ok(views.html.index(filterForm.fill(userData), lessonList))
        }
      }
    )
  }
}
