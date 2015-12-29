package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Lesson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MathFacAPI extends Controller {

    private class Foo {
        List<String> teachers;
    }

    public static Result smartSearch(String parameters) {

        List<Lesson> teachersList = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(parameters);
            JSONArray teachersArray = (JSONArray) jsonObject.get("teachers");
            for(int i=0; i<teachersArray.size();i++){
                teachersList.addAll(Lesson.find.where().ilike("teacher", "%" + teachersArray.get(i) + "%")
                        .orderBy("teacher asc, day asc, hours asc").findList() );
            }
        } catch (Exception e) {
            System.out.println("ERROR");
        }

        String table =
                "<div>\n" +
                "<table>\n" ;
        for (int i=0; i < teachersList.size(); i++) {
            Lesson lesson = teachersList.get(i);
            table += "<tr><td>" + lesson.teacher + "</td><td>" + lesson.day + " " + lesson.hours + "</td><td>" + lesson.room + "</td></tr>\n";
        }
        table += "</table>\n" +
                "</div>\n";


        response().setContentType("text/html; charset=utf-8");

        return ok(table);
    }
}
