package controllers

import java.util.concurrent.TimeUnit

import models.{Lesson, ScheduleURL}
import play.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{Action, Controller}
import play.libs.Akka

import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration

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

  def lessonSorter(l1: Lesson, l2: Lesson): Boolean = {
    (l1.getDayOfWeek < l2.getDayOfWeek())
  }

  def testHTMLLinks() = Action {
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
    val fullLinks = excelLinks.map(s => if (s.startsWith("http://")) s else "http://math.isu.ru"+s )

    for (link <-fullLinks) {
      println(link)
    }
    val t = fullLinks.mkString("","\n","")
    Ok(t)
  }
}
