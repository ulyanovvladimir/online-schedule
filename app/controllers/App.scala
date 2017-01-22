package controllers

import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

import models.{Lesson, ScheduleURL}
import parser.Parser
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{Action, Controller}
import play.libs.Akka
import views.html.defaultpages.todo

import scala.collection.JavaConversions._
import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * @author Vladimir Ulyanov
  */
object App extends Controller {

  case class FilterData(groupNumber: Option[String], instructor: List[String])


  val filterForm = Form(
    mapping(
      "groupNumber" -> optional(text),
      "instructor" -> list(text)
    )(FilterData.apply)(FilterData.unapply)
  )

  def allGroups = Lesson.all().toList.map(lesson => lesson.getGroupNumber).distinct.sorted

  def allInstructors = Lesson.all().toList.map(lesson => lesson.getInstructor).distinct.sorted

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
          case FilterData(None, Nil) =>
            val lessonList = Lesson.find.orderBy("dayOfWeek asc, fromHours asc").findList()
            Ok(views.html.index(filterForm.fill(userData), lessonList))
          case FilterData(Some(group), Nil) =>
            val lessonList = Lesson.find.where().ilike("groupNumber", group).orderBy("dayOfWeek asc, fromHours asc").findList()
            Ok(views.html.index(filterForm.fill(userData), lessonList))
          case FilterData(None, instructors) =>
            val lessonList = Lesson.find.orderBy("dayOfWeek asc, fromHours asc").findList().
              filter(lesson => instructors.contains(lesson.getInstructor))
            Ok(views.html.index(filterForm.fill(userData), lessonList))
          case FilterData(Some(group), instructors) =>
            val lessonList = Lesson.find.where().ilike("groupNumber", group).orderBy("dayOfWeek asc, fromHours asc")
              .findList().filter(lesson => instructors.contains(lesson.getInstructor))
            Ok(views.html.index(filterForm.fill(userData), lessonList))
        }
      }
    )
  }

  case class InstructorFormData(instructor: Option[String])

  val instructorsForm = Form(
    mapping(
      "instructor" -> optional(text)
    )(InstructorFormData.apply)(InstructorFormData.unapply)
  )

  def instructorSchedule() = Action { implicit request =>
    instructorsForm.bindFromRequest.fold(
      formWithErrors => {
        //form contains error(s)
        BadRequest(views.html.instructors(formWithErrors))
      },
      userData => {
        userData match{
          case InstructorFormData(None) =>
            Ok(views.html.instructors(instructorsForm.fill(userData)))
          case InstructorFormData(Some(instructor)) =>
            Redirect(controllers.routes.Application.instructorCalendar(instructor))
        }
      }
    )
  }

  def lessonSorter(l1: Lesson, l2: Lesson): Boolean = {
    (l1.getDayOfWeek < l2.getDayOfWeek())
  }

  def startReload() = Action {
    val t: String = reload
    Ok(t)
  }

  def reload: String = {
    import net.ruippeixotog.scalascraper.browser.JsoupBrowser
    import net.ruippeixotog.scalascraper.dsl.DSL._
    import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
    import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
    import net.ruippeixotog.scalascraper.model.Element

    //download html
    val browser = JsoupBrowser()
    val doc = browser.get("http://math.isu.ru/ru/students/index.html")
    //parse links

    val links = doc >> elementList(".page_content a") >> attr("href")("a")
    val excelLinks = links.filter(s => s.endsWith("xls") || s.endsWith("xlsx"))
    val fullLinks = excelLinks.map(s => if (s.startsWith("http://")) s else "http://math.isu.ru" + s)

    for (link <- fullLinks) {
      println(link)
    }
    val t = fullLinks.mkString("", "\n", "")

    import play.api.libs.concurrent.Execution.Implicits._

    Akka.system.scheduler.scheduleOnce(FiniteDuration(0, TimeUnit.MILLISECONDS)) {
      println("I'm scheduled")
      val before: Long = System.currentTimeMillis
      Lesson.clearBase()
      import scala.collection.JavaConversions._
      for (url <- fullLinks) {
        Logger.info(url)
        try {
          val list = Parser.parseURL(new URL(url))
          for (lesson <- list) {
            models.Lesson.from(lesson).save()
          }
        }
        catch {
          case e: IOException => {
            Logger.error("Can not parse the URL:" + url)
          }
        }
      }
      Logger.info("downloaded and processed for " + (System.currentTimeMillis - before) + " ms.")
    }
    t
  }
}
