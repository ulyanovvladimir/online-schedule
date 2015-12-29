package controllers;

import models.Lesson;
import models.Admin;
import models.ScheduleURL;
import play.*;
import play.mvc.*;
import scala.collection.immutable.Stream;
import views.html.*;
import play.data.*;
import java.io.*;
import static play.data.Form.form;
import java.util.*;
import play.db.ebean.*;
import play.libs.Crypto;
import play.mvc.Security.Authenticated;


public class Application extends Controller {

  static Form<ScheduleURL> urlForm = form(ScheduleURL.class);

  public static Result index() {
	List<Lesson> lessonList = Lesson.all();
    return ok(
      views.html.index.render(lessonList)
	);
  }

  private static void foo(Map<Lesson, Integer> map, String field, String part) {
  	List<Lesson> lessonList = Lesson.find.where().ilike(field, "%" + part + "%")
  			.orderBy("groupNumber asc, day asc, hours asc").findList();

  	for (int j=0; j<lessonList.size(); j++){
  		Lesson key = lessonList.get(j);
  		if (!map.containsKey(key)){
  			map.put(key, 1);
  		} else {
  			map.put(key, map.get(key) + 1);
  		}
  	}
  }

  public static Result smartSearch() {
  	DynamicForm requestData = Form.form().bindFromRequest();
  	String[] parts = requestData.get("smartSearchField").trim().split(" ");
  	Map<Lesson,Integer> map = new LinkedHashMap<>();
  	List<Lesson> response = new ArrayList<>();

  	for (int i=0; i < parts.length; i++) {
  		foo(map, "groupNumber", parts[i]);
  		foo(map, "day", 		parts[i]);
  		foo(map, "hours", 		parts[i]);
  		foo(map, "lecture", 	parts[i]);
  		foo(map, "teacher", 	parts[i]);
  		foo(map, "room", 		parts[i]);
  	}

  	for (Map.Entry<Lesson, Integer> pair : map.entrySet()){
  		if (pair.getValue() >= parts.length){
  			response.add(pair.getKey());
  		}
  	}

  	return ok(
  			views.html.index.render(response)
  	);
  }
  
  @Authenticated(Secured.class)
  public static Result adminPage() {
	List<Lesson> lessonList = new ArrayList<>();
	List<ScheduleURL> urlList = ScheduleURL.all();
    return ok(
      views.html.adminPage.render(lessonList, urlList, urlForm)
	);
  }
  
  public static Result signInPage() {
    return ok(
      views.html.signInPage.render()
	);
  }

  public static Result logIn() {
	DynamicForm requestData = Form.form().bindFromRequest();
	List<Admin> adminList = Admin.find.where()
		.ilike("username", requestData.get("username"))
	.findList();
	Admin admin;
	try {
		admin = adminList.get(0);
			if(admin != null){
				if(admin.userpass.equals(Crypto.encryptAES(requestData.get("userpass")))){
					session("admin", requestData.get("username"));
					return redirect(controllers.routes.Application.adminPage());
				}
			}
	} catch(Exception e) {
		session().clear();
		return badRequest(views.html.signInPage.render());
	}
  return badRequest(views.html.signInPage.render());
  }

  public static Result insertLessons() throws Exception {
	DynamicForm requestData = Form.form().bindFromRequest();
	Lesson lesson = new Lesson();
	lesson.groupNumber = requestData.get("groupNumber");
	lesson.day = requestData.get("day");
	lesson.hours = requestData.get("hours");
	lesson.lecture = requestData.get("lecture");
	lesson.teacher = requestData.get("teacher");
	lesson.room = requestData.get("room");
	lesson.save();
	return redirect(controllers.routes.Application.adminPage());
  }

  public static Result insertURL() {
    DynamicForm requestData = Form.form().bindFromRequest();
    ScheduleURL url = new ScheduleURL();
	url.url = requestData.get("urlFieldValue");
	url.save();
	return redirect(controllers.routes.Application.adminPage());
  }

  public static Result deleteURL(Integer id) {
    ScheduleURL.delete(id);
  	return redirect(controllers.routes.Application.adminPage());
  }

  public static Result editURLPage(Integer id) {
    System.out.println("LOGGGGG"+id);
    ScheduleURL url = ScheduleURL.find.ref(id);

  	if (url==null) {
  		return notFound();
  	}
  	Form<ScheduleURL> filledForm = urlForm.fill(url);
  	return ok(
  			views.html.editURLPage.render(filledForm)
  	);
  }

  public static Result editURL() {
  	Form<ScheduleURL> filledForm = urlForm.bindFromRequest();
  	if(filledForm.hasErrors()) {
  		return badRequest(
  				views.html.editURLPage.render(filledForm)
  		);
  	} else {
		ScheduleURL.edit(filledForm.get());
  		return redirect(controllers.routes.Application.adminPage());
  	}
  }

}