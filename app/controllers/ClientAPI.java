package controllers;

import models.Lesson;
import play.*;
import play.mvc.*;
import views.html.*;
import play.data.*;
import java.io.*;
import java.util.*;
import play.db.ebean.*;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ClientAPI extends Controller {
  
  public static Result findLessons(String groupNumber, String day, String hours, String lecture, String teacher, String room) {
  
    List<Lesson> lessonList = Lesson.find.where()
		.ilike("groupNumber", "%" + groupNumber + "%")
		.ilike("day", "%" + day + "%")
		.ilike("hours", "%" + hours + "%")
		.ilike("lecture", "%" + lecture + "%")
		.ilike("teacher", "%" + teacher + "%")
		.ilike("room", "%" + room + "%")
		.findList();

	ObjectNode result = Json.newObject();
    JsonNode lessonsListJson = Json.toJson(lessonList);
	
	result.put("objects", lessonsListJson);
	
	return ok(result);
  }
  
  
  }