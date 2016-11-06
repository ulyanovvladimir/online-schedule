package models;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlUpdate;
import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;


@Entity
public class Lesson extends Model {

    @Id
    private Integer id;
    private String groupNumber;
    private String day;
    private Integer dayOfWeek;
    private String hours;
    private String lecture;
    private String teacher;
    private String room;

    private Integer fromHours;
    private Integer fromMinutes;
    private Integer toHours;
    private Integer toMinutes;

    public static Finder<Integer, Lesson> find = new Finder(
            Integer.class, Lesson.class
    );

    public static List<Lesson> all() {
        return find.all();
    }

    public static void clearBase() {
        SqlUpdate rebuildTable = Ebean.createSqlUpdate("TRUNCATE TABLE lesson");
        try {
            rebuildTable.execute();
        } catch (Exception e) {
            System.out.println("LOGGGGG" + e.getMessage());
        }

    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getFromHours() {
        return fromHours;
    }

    public void setFromHours(Integer fromHours) {
        this.fromHours = fromHours;
    }

    public Integer getFromMinutes() {
        return fromMinutes;
    }

    public void setFromMinutes(Integer fromMinutes) {
        this.fromMinutes = fromMinutes;
    }

    public Integer getToHours() {
        return toHours;
    }

    public void setToHours(Integer toHours) {
        this.toHours = toHours;
    }

    public Integer getToMinutes() {
        return toMinutes;
    }

    public void setToMinutes(Integer toMinutes) {
        this.toMinutes = toMinutes;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
}