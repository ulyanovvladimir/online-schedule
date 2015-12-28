package controllers;

import models.Lesson;
import models.Admin;
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
  
  public static Result index() {
	List<Lesson> lessonList = Lesson.all();
    return ok(
      views.html.index.render(lessonList)
	);
  }
  
  @Authenticated(Secured.class)
  public static Result admin() {
	List<Lesson> lessonList = new ArrayList<Lesson>();
    return ok(
      views.html.admin.render(lessonList)
	);
  }
  
  public static Result signIn() {
    return ok(
      views.html.signIn.render()
	);
  }

  public static Result logIn() {

  	DynamicForm requestData = Form.form().bindFromRequest();
  	List<Admin> adminList = Admin.find.where()
  		.ilike("username", requestData.get("username"))
  	.findList();
  	Admin admin = new Admin();
  	try {
  		admin = adminList.get(0);
  			if(admin != null){
  				if(admin.userpass.equals(Crypto.encryptAES(requestData.get("userpass")))){
  					session("admin", requestData.get("username"));
  					return redirect(controllers.routes.Application.admin());
  				}
  			}
  	} catch(Exception e) {
  		session().clear();
  		return badRequest(views.html.signIn.render());
  	}
  return badRequest(views.html.signIn.render());

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
	return redirect(controllers.routes.Application.admin());
  }

  public static Result findLessons() {
    DynamicForm requestData = Form.form().bindFromRequest();
    List<Lesson> lessonList = Lesson.find.where()
        .ilike("groupNumber", "%" + requestData.get("groupNumber") + "%")
        .ilike("day", "%" + requestData.get("day") + "%")
        .ilike("hours", "%" + requestData.get("hours") + "%")
        .ilike("lecture", "%" + requestData.get("lecture") + "%")
        .ilike("teacher", "%" + requestData.get("teacher") + "%")
        .ilike("room", "%" + requestData.get("room") + "%")
    .findList();
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
	  List<Lesson> request = new ArrayList<Lesson>();

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
			  request.add(pair.getKey());
		  }
	  }

	  return ok(
		  views.html.index.render(request)
	  );
  }

}