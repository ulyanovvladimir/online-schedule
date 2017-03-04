package controllers

import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

import models.{Lesson, ScheduleURL, WeekDays}
import parser.Parser
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.libs.Akka

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

  case class GroupFormData(group: Option[String])

  val groupForm = Form(
    mapping(
      "group" -> optional(text)
    )(GroupFormData.apply)(GroupFormData.unapply)
  )

  def groupSchedule() = Action { implicit request =>
    groupForm.bindFromRequest.fold(
      formWithErrors => {
        //form contains error(s)
        BadRequest(views.html.groups(formWithErrors))
      },
      userData => {
        userData match {
          case GroupFormData(None) =>
            Ok(views.html.groups(groupForm.fill(userData)))
          case GroupFormData(Some(group)) =>
            Redirect(controllers.routes.App.groupCalendar(group))
        }
      }
    )
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
            Redirect(controllers.routes.App.instructorCalendar(instructor))
        }
      }
    )
  }

  def instructorCalendar(instructor: String) = Action {
    val lessons = Lesson.find.where.ilike("instructor", "%" + instructor + "%").orderBy("dayOfWeek asc, fromHours asc").findList //todo filter
    val wd = WeekDays.find.all().get(0)
    //render as UTF-8 binary
    Ok(views.txt.calendar.render(lessons, wd).body.getBytes(Charset.forName("UTF-8"))) /*.as("text/iCalendar")*/
  }

  def groupCalendar(group: String) = Action {
    val lessons = Lesson.find.where.ilike("groupNumber", "%" + group + "%").orderBy("dayOfWeek asc, fromHours asc").findList //todo filter
    //render as UTF-8 binary
    Ok(views.txt.groupcalendar.render(lessons).body.getBytes(Charset.forName("UTF-8"))) /*.as("text/iCalendar")*/
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
    val config = play.api.Play.current.configuration
    val page = config.getString("download.url").get
    val doc = browser.get(page)
    //parse links

    val links = doc >> elementList("a[href]") >> attr("href")("a")
    val excelLinks = links.filter(s => s.endsWith("xls") || s.endsWith("xlsx"))

    val domain  = config.getString("download.domain").get
    val fullLinks = excelLinks.map(s => if (s.startsWith("http://")) s else domain + s)
    fullLinks foreach println

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
