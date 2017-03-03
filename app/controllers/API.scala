package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}


/**
  * @author Vladimir Ulyanov
  */
object API extends Controller {
  /**
    *
    * @return JSON array of instructors in alphabetical order
    */
  def instructors() = Action {
    Ok(Json.toJson(App.allInstructors))
  }

  def lessons() = TODO
}
